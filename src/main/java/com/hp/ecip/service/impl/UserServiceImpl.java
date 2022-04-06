package com.hp.ecip.service.impl;

import java.io.File;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.ecip.bo.Route;
import com.hp.ecip.bo.User;
import com.hp.ecip.mapper.RouteMapper;
import com.hp.ecip.mapper.UserMapper;
import com.hp.ecip.service.UserService;
import com.hp.ecip.util.ShellExecUtil;

@Service
public class UserServiceImpl implements UserService{
	
	private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RouteMapper routeMapper;
	
	@Value("${sftp.instance_id}")
	private String instanceId;
	
	@Value("${sftp.home_path}")
	private String homePath;
	
	@Value("${sftp.create_shell_path}")
	private String createShellPath;
	
	@Value("${sftp.admin.password}")
	private String adminPassword;
	
	@Override
	public boolean setUserAuthAndRoute(String instanceId){
		
		boolean status = true;
		List<User> userList = null;
//		List<Route> oRouteList = null;
//		List<Route> hRouteList = null;
		try {
			userList = userMapper.getUserListByInstanceId(instanceId);
			log.info("==== userList size : " + userList.size());
//			oRouteList = routeMapper.getORouteListByInstanceId(instanceId);
//			log.info("==== oRouteList size : " + oRouteList.size());
//			hRouteList = routeMapper.getHRouteListByInstanceId(instanceId);
//			log.info("==== hRouteList size : " + hRouteList.size());
		} catch (Exception e) {
//			log.error("UserServiceImpl setUserAuthAndRoute 获取userList, oRouteList, hRouteList异常", e);
			log.error("UserServiceImpl setUserAuthAndRoute 获取userList异常", e);
			status = false;
		}
			
		//加载用户、上传目录、下载目录
		if(userList != null && userList.size() > 0){
			log.info("========加载用户权限、上传目录、下载目录、备份目录开始========");
			for(User user : userList){
				String userName = user.getUserName();
				if("admin".equals(userName)){
					continue;
				}
				
				String passwordBase64 = user.getUserPassword();
				byte[] bytes = Base64.decode(passwordBase64.getBytes());
				String password = new String(bytes);
				String userPartyId = user.getPartyId();
				
				//创建incoming目录
				String userPartyIdIncoming = homePath + File.separator + userPartyId + File.separator + "incoming";
				File incomingFile = new File(userPartyIdIncoming);
				if(!incomingFile.exists()){
					incomingFile.mkdirs();
				}
				//创建outgoing目录
				String userPartyIdOutgoing = homePath + File.separator + userPartyId + File.separator + "outgoing";
				File outgoingFile = new File(userPartyIdOutgoing);
				if(!outgoingFile.exists()){
					outgoingFile.mkdirs();
				}
				//创建Complete目录,在create.sh脚本中将Complete目录的属组赋为root
				String userPartyIdComplete = homePath + File.separator + userPartyId + File.separator + "Complete";
				File completeFile = new File(userPartyIdComplete);
				if(!completeFile.exists()){
					completeFile.mkdirs();
				}
								
				//创建dev目录,在create.sh脚本中将dev目录的属组赋为root,为sftp用户增加log做的配置目录
				String userPartyIdDev = homePath + File.separator + userPartyId + File.separator + "dev";
				File devFile = new File(userPartyIdDev);
				if(!devFile.exists()){
					devFile.mkdirs();
				}
				
				String userHomePartyId = homePath + File.separator + userPartyId;
				String userParams = userName + ":" + password + ":" + userHomePartyId;
				log.info("======开始执行加载用户incoming、outgoing、Complete目录脚本，参数： " + createShellPath + " " + userName + ":password:" + userHomePartyId);
				try {
					String userCreateResult = ShellExecUtil.execShell(createShellPath + " " + userParams);
					//log.info("执行加载用户incoming、outgoing、Complete目录脚本的结果是: " + userCreateResult);
				} catch (Exception e) {
					log.error("执行加载用户incoming、outgoing、Complete目录脚本发生异常 " , e);
					status = false;
				}
				
				//向/etc/rsyslog.d/sftp.conf文件添加每个sftp用户目录的log配置
				String userLogConfigPath =  userPartyIdDev + File.separator + "log";//每个sftp用户的sftp log的目录
				String logResult1 = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "grep " + userLogConfigPath + " /etc/rsyslog.d/sftp.conf"});				
				if(logResult1 != null && !"".equals(logResult1)){
					log.info("grep userLogConfig的结果 =========== " + logResult1);
				}else{
					log.info("将用户的log配置写入/etc/rsyslog.d/sftp.conf文件======开始");
					String userLogConfigReplace  = "\"" + userLogConfigPath + "\"" ;//使用转义字符在字符串里增加双引号
					String userLogConfigStr = "sed -i '1i\\input(type=\"imuxsock\" Socket="+ userLogConfigReplace +" CreatePath=\"on\")'" + " /etc/rsyslog.d/sftp.conf";
					ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", userLogConfigStr});
					log.info("将用户的log配置写入/etc/rsyslog.d/sftp.conf文件======结束");
				}
				log.info("======执行加载用户incoming、outgoing、Complete、dev目录脚本结束");

			}
			log.info("========加载用户权限、上传目录、下载目录、备份目录结束========");
		}
		
		
		//创建sftpadmin用户，用于sftp连通性验证测试
		log.info("========创建sftpadmin用户开始========");
		byte[] bytes = Base64.decode(adminPassword.getBytes());
		String decryptAdminPassword = new String(bytes);
		
		String sftpadminIncoming = homePath + File.separator + "SFTPADMIN" + File.separator + "incoming";
		File sftpadminIncomingFile = new File(sftpadminIncoming);
		if(!sftpadminIncomingFile.exists()){
			sftpadminIncomingFile.mkdirs();
		}
		
		String sftpadminOutgoing = homePath + File.separator + "SFTPADMIN" + File.separator + "outgoing";
		File sftpadminOutgoingFile = new File(sftpadminOutgoing);
		if(!sftpadminOutgoingFile.exists()){
			sftpadminOutgoingFile.mkdirs();
		}
		
		String sftpadminComplete = homePath + File.separator + "SFTPADMIN" + File.separator + "Complete";
		File sftpadminCompleteFile = new File(sftpadminComplete);
		if(!sftpadminCompleteFile.exists()){
			sftpadminCompleteFile.mkdirs();
		}
		
		String sftpadminDev = homePath + File.separator + "SFTPADMIN" + File.separator +  "dev";
		File sftpadminDevFile = new File(sftpadminDev);
		if(!sftpadminDevFile.exists()){
			sftpadminDevFile.mkdirs();
		}
		
		
		try {
			String adminCreateResult = ShellExecUtil.execShell(createShellPath + " " + "sftpadmin:" + decryptAdminPassword + ":" + homePath + File.separator + "SFTPADMIN");
			//log.info("执行加载sftpadmin用户incoming、outgoing、Complete目录脚本的结果是: " + adminCreateResult);
		} catch (Exception e) {
			log.error("========创建sftpadmin用户发生异常", e);
		}
		//向/etc/rsyslog.d/sftp.conf文件添加每个sftpadmin用户目录的log配置
		String sftpadminLogConfigPath =  sftpadminDev + File.separator + "log";//sftpadmin用户的sftp log的目录
		String sftpadminLogResult = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "grep " + sftpadminLogConfigPath + " /etc/rsyslog.d/sftp.conf"});
		if(sftpadminLogResult != null && !"".equals(sftpadminLogResult)){
			log.info("grep sftpadminLogConfig的结果 =========== " + sftpadminLogResult);
		}else{
			log.info("将sftpadmin用户的log配置写入/etc/rsyslog.d/sftp.conf文件======开始");
			String sftpadminLogConfigReplace  = "\"" + sftpadminLogConfigPath + "\"" ;//使用转义字符在字符串里增加双引号
			String sftpadminLogConfigStr = "sed -i '1i\\input(type=\"imuxsock\" Socket="+ sftpadminLogConfigReplace +" CreatePath=\"on\")'" + " /etc/rsyslog.d/sftp.conf";
			ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", sftpadminLogConfigStr});
			log.info("将sftpadmin用户的log配置写入/etc/rsyslog.d/sftp.conf文件======结束");
		}

		log.info("========创建sftpadmin用户结束========");
		
		
		//查看rsyslogd的进程，如果存在，kill掉，然后启动rsyslogd		
		String logResult3 = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "cat /var/run/syslogd.pid"});
		log.info("rsyslogd的进程号是=========== " + logResult3 + " ,如果线程号有值，将其kill，然后重新启动rsyslogd，如值为空，正常启动rsyslogd");
		if(logResult3 != null && !"".equals(logResult3)){
			ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "kill -9 " + logResult3});				
		}
		ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "rsyslogd"});
		
		return status;
	}
	
	
	@Override
	public boolean setUserAuthByUserId(String userId){
		log.info("========刷新userId为 " + userId + " 的用户目录及其子目录权限开始========");
		boolean status = true;
		User user = userMapper.getUserByUserId(userId);
		String userName = user.getUserName();
		if(!userName.equals("admin") && !userName.equals("sftpadmin")){
			String passwordBase64 = user.getUserPassword();
			byte[] bytes = Base64.decode(passwordBase64.getBytes());
			String password = new String(bytes);
			String userPartyId = user.getPartyId();
			
			//创建incoming目录
			String userPartyIdIncoming = homePath + File.separator + userPartyId + File.separator + "incoming";
			File incomingFile = new File(userPartyIdIncoming);
			if(!incomingFile.exists()){
				incomingFile.mkdirs();
			}
			//创建outgoing目录
			String userPartyIdOutgoing = homePath + File.separator + userPartyId + File.separator + "outgoing";
			File outgoingFile = new File(userPartyIdOutgoing);
			if(!outgoingFile.exists()){
				outgoingFile.mkdirs();
			}
			//创建Complete目录,在create.sh脚本中将Complete目录的属组赋为root
			String userPartyIdComplete = homePath + File.separator + userPartyId + File.separator + "Complete";
			File completeFile = new File(userPartyIdComplete);
			if(!completeFile.exists()){
				completeFile.mkdirs();
			}
							
			//创建dev目录,在create.sh脚本中将dev目录的属组赋为root,为sftp用户增加log做的配置目录
			String userPartyIdDev = homePath + File.separator + userPartyId + File.separator + "dev";
			File devFile = new File(userPartyIdDev);
			if(!devFile.exists()){
				devFile.mkdirs();
			}
			
			String userHomePartyId = homePath + File.separator + userPartyId;
			String userParams = userName + ":" + password + ":" + userHomePartyId;
			log.info("======开始执行加载用户incoming、outgoing目录脚本，参数： " + createShellPath + " " + userName + ":password:" + userHomePartyId);
			try {
				String userCreateResult = ShellExecUtil.execShell(createShellPath + " " + userParams);
				//log.info("执行加载用户incoming、outgoing、Complete目录脚本的结果是: " + userCreateResult);
			} catch (Exception e) {
				log.error("执行加载用户incoming、outgoing目录脚本发生异常 " , e);
				status = false;
			}
			
			//向/etc/rsyslog.d/sftp.conf文件添加每个sftp用户目录的log配置
			String userLogConfigPath =  userPartyIdDev + File.separator + "log";//每个sftp用户的sftp log的目录
			String logResult1 = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "grep " + userLogConfigPath + " /etc/rsyslog.d/sftp.conf"});				
			if(logResult1 != null && !"".equals(logResult1)){
				log.info("grep userLogConfig的结果 =========== " + logResult1);
			}else{
				log.info("将用户的log配置写入/etc/rsyslog.d/sftp.conf文件======开始");
				String userLogConfigReplace  = "\"" + userLogConfigPath + "\"" ;//使用转义字符在字符串里增加双引号
				String userLogConfigStr = "sed -i '1i\\input(type=\"imuxsock\" Socket="+ userLogConfigReplace +" CreatePath=\"on\")'" + " /etc/rsyslog.d/sftp.conf";
				ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", userLogConfigStr});
				log.info("将用户的log配置写入/etc/rsyslog.d/sftp.conf文件======结束");
			}
			
			log.info("======执行加载用户incoming、outgoing、Complete、dev目录脚本结束");
			
			//查看rsyslogd的进程，如果存在，kill掉，然后启动rsyslogd		
			String logResult3 = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "cat /var/run/syslogd.pid"});
			log.info("rsyslogd的进程号是=========== " + logResult3 + " ,如果线程号有值，将其kill，然后重新启动rsyslogd，如值为空，正常启动rsyslogd");
			if(logResult3 != null && !"".equals(logResult3)){
				ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "kill -9 " + logResult3});
			}
			ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "rsyslogd"});
		}
		log.info("========刷新userId为 " + userId + " 的用户目录及其子目录权限结束========");
		return status;
	}
	
	
	@Override
	public boolean setORouteAuthByRouteId(String routeId){
		log.info("========刷新routeId为" + routeId + "的发起方路由目录权限开始========");
		boolean status = true;
		try {
			Route route = routeMapper.getORouteByRouteId(routeId);
			String oUserName = route.getoUserName();
			log.info("该路由的发起方用户名是：" + oUserName);
			String oPartyId = route.getoPartyId();
			String oPath = route.getoPath();
			String userORoutePath = homePath + File.separator + oPartyId + File.separator + oPath;
			log.info("该路由的发起方目录全路径是：" + userORoutePath);
			File userORoutePathFile = new File(userORoutePath);
			if(!userORoutePathFile.exists()){
				userORoutePathFile.mkdirs();
			}
			String afterReplacePath = userORoutePath.replace(homePath + File.separator + oPartyId + File.separator, "");
			int index = afterReplacePath.indexOf("/");//替换后的目录，第一次出现 / 的位置
			String subPath = "";
			if(index == -1){
				subPath = afterReplacePath;
			}else{
				subPath = afterReplacePath.substring(0, index);
			} 
			String fuquanPath = homePath + File.separator + oPartyId + File.separator + subPath;
			log.info("该路由的发起方需要授权的目录是：" + fuquanPath);
			String oRouteResult = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "chown -R " + oUserName + ":users " + fuquanPath});
			log.info("执行加载发起方路由脚本的结果是：" + oRouteResult);
		} catch (Exception e) {
			status = false;
			log.error("======创建发起方路由目录并授权的过程中发生异常", e);
		}
		log.info("========刷新routeId为" + routeId + "的发起方路由目录权限结束========");
		return status;
	}
	
	@Override
	public boolean setHRouteAuthByRouteId(String routeId){
		log.info("========刷新routeId为" + routeId + "的落地方路由目录权限开始========");
		boolean status = true;
		try {
			Route route = routeMapper.getHRouteByRouteId(routeId);
			String hUserName = route.gethUserName();
			log.info("该路由的落地方用户名是：" + hUserName);
			String hPartyId = route.gethPartyId();
			String hPath = route.gethPath();
			String userHRoutePath = homePath + File.separator + hPartyId + File.separator + hPath;
			log.info("该路由的落地方目录全路径是：" + userHRoutePath);
			File userHRoutePathFile = new File(userHRoutePath);
			if(!userHRoutePathFile.exists()){
				userHRoutePathFile.mkdirs();
			}
			String afterReplacePath = userHRoutePath.replace(homePath + File.separator + hPartyId + File.separator, "");
			int index = afterReplacePath.indexOf("/");//替换后的目录，第一次出现 / 的位置
			String subPath = "";
			if(index == -1){
				subPath = afterReplacePath;
			}else{
				subPath = afterReplacePath.substring(0, index);
			}
			String fuquanPath = homePath + File.separator + hPartyId + File.separator + subPath;
			log.info("该路由的落地方需要授权的目录是：" + fuquanPath);			
			String hRouteResult = ShellExecUtil.excuteShellCmd(new String[]{"/bin/bash", "-c", "chown -R " + hUserName + ":users " + fuquanPath});
			log.info("执行加载落地方路由脚本的结果是：" + hRouteResult);
		} catch (Exception e) {
			status = false;
			log.error("======创建落地方路由目录并授权的过程中发生异常", e);
		}
		log.info("========刷新routeId为" + routeId + "的落地方路由目录权限结束========");
		return status;
	}
	
	
//	public static void main(String[] args) {
//		String sbc = "/opt/ecip/F/SFTPServer/home/CSYH0722/CSVB1234/incoming/0722";
//		System.out.println("==================" + sbc.substring(0,0));
//		String a=  sbc.replace("/opt/ecip/F/SFTPServer/home/CSYH0722/", "");
//		System.out.println(a);
//		int b = a.indexOf("/");
//		System.out.println(b);
//		String c = a.substring(0, b);
//		System.out.println(c);
//		int i = sbc.indexOf("/opt/ecip/F/SFTPServer/home/CSYH0722");
//		System.out.println(i);
//		
//	}
}
