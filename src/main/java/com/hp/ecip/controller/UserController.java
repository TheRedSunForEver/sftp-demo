package com.hp.ecip.controller;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.ecip.service.UserService;
import com.hp.ecip.util.SftpUtil;
import com.jcraft.jsch.ChannelSftp;

@RestController
public class UserController {
	
	private static Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Value("${sftp.instance_id}")
	private String instanceId;
	
	@Value("${sftp.admin.password}")
	private String adminPassword;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/sftpserver/reload", method = RequestMethod.POST)
	public String reload(){
		log.info("====UserController reload begin...");
		log.info("====UserController reload instanceId: " + instanceId);
		String responseStr = "reload user and route path successfully!";
		boolean status = userService.setUserAuthAndRoute(instanceId);
		if(status == false){
			responseStr = "reload user and route path happens Exception!";
		}
		log.info(responseStr);
		log.info("====UserController reload end...");
		return responseStr;
	}
	
	@RequestMapping(value = "/sftpserver/user", method = RequestMethod.POST)
	public String setUserAuth(@RequestParam("userId") String userId){
		log.info("====UserController setUserAuth begin");
		String responseStr = "刷新用户目录成功！用户id: " + userId;
		boolean b = userService.setUserAuthByUserId(userId);
		if(b == false){
			responseStr = "刷新用户目录发生异常！用户id: " + userId;
		}
		log.info("====UserController setUserAuth end");
		return responseStr;
	}
	
	
	@RequestMapping(value = "/sftpserver/oroute", method = RequestMethod.POST)
	public String setORouteAuth(@RequestParam("routeId") String routeId){
		log.info("====UserController setORouteAuth begin");
		String responseStr = "刷新发起方路由目录成功！路由id: " + routeId;
		boolean b = userService.setORouteAuthByRouteId(routeId);
		if(b == false){
			responseStr = "刷新发起方路由目录发生异常！路由id: " + routeId;
		}
		log.info("====UserController setORouteAuth end");
		return responseStr;
	}
	
	@RequestMapping(value = "/sftpserver/hroute", method = RequestMethod.POST)
	public String setHRouteAuth(@RequestParam("routeId") String routeId){
		log.info("====UserController setHRouteAuth begin");
		String responseStr = "刷新落地方路由目录成功！路由id: " + routeId;
		boolean b = userService.setHRouteAuthByRouteId(routeId);
		if(b == false){
			responseStr = "刷新落地方路由目录发生异常！路由id: " + routeId;
		}
		log.info("====UserController setHRouteAuth end");
		return responseStr;
	}
	
	
	@RequestMapping(value = "/sftpserver/sftpadmin/check", method = RequestMethod.GET)
	public String sftpadminCheck(){
		byte[] bytes = Base64.decode(adminPassword.getBytes());
		String decryptPassword = new String(bytes);
		log.info("====UserController sftpadminCheck begin...");
		String message = "sftpadmin is not connected";
		ChannelSftp channel = SftpUtil.getChannel("localhost", 22, "sftpadmin", decryptPassword);
		if(channel.isConnected()){
			log.info("====sftpadmin用户连接sftp服务器成功!");
			message = "sftpadmin is connected";
		}
		log.info("====message: " + message);
		SftpUtil.closeChannel(channel);
		log.info("====UserController sftpadminCheck end...");
		return message;
	}
	
}
