package com.hp.ecip.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SftpUtil.class);
	
	public static ChannelSftp getChannel(String sftpIP, Integer sftpPort, String sftpUserName, String sftpPassWord) {
		//创建JSch对象
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		try {
			session = jsch.getSession(sftpUserName, sftpIP, sftpPort);
			log.info("Session created: " + session);
			session.setPassword(sftpPassWord);			
			Properties config = new Properties();
			config.setProperty("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
		
			log.info("session connected");
			log.info("opening channel");
			channel = session.openChannel("sftp");
			channel.connect();
			log.info("connected successfully to sftpHost = " + sftpIP + ", sftpUserName= " + sftpUserName + ", returning: " + channel);
			return (ChannelSftp)channel;
		} catch (Exception e) {
			log.error("sftp connect failed, please check configuration ", e);
		}
		return null;		
	}
	
	//关闭sftp连接
	public static void closeChannel(ChannelSftp channel) {
		try {
			if(channel != null){
				channel.disconnect();
				if(channel.getSession() != null){
					channel.getSession().disconnect();
				}
			}
		} catch (Exception e) {
			log.error("sftp channel close failed ", e);
		}
	}
	
	public static void uploadFileToSftp(ChannelSftp channel, String sftpPath, File file){
		try {
//			if(!sftpPath.equals("")){				
//				channel.cd(sftpPath);
//	        }
			channel.put(file.getAbsolutePath(), sftpPath + "/" + file.getName(), ChannelSftp.OVERWRITE);//使用OVERWRITE模式
			log.info("local file path " + file.getAbsolutePath());
			log.info("remote sftp server path " + sftpPath);
		} catch (Exception e) {
			log.error("sftp upload file failed ", e);
		}
	}
	
	public static void downloadFileFromSftp(ChannelSftp channel, String sftpPath, String localPath, String fileName){
		try {
			Vector<?> vector = channel.ls(sftpPath);
			for(Object item : vector) {
				ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)item;
				String sftpFileName = entry.getFilename();
				if (sftpFileName.equals(fileName)) {
					File localFile = new File(localPath + "/" + sftpFileName);
					OutputStream is = new FileOutputStream(localFile);
					channel.get(sftpPath + "/" + sftpFileName, is);
					is.close();
				}
			}
		} catch (Exception e){
			log.error("sftp download file failed ", e);
		}
	}
	
	
	public static void deleteFileInSftp(ChannelSftp channel, String sftpPath, List<String> fileNameList){
		try {
			for (String fileName : fileNameList) {
				Vector<?> vector = channel.ls(sftpPath);
				for (Object item : vector) {
					ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) item;
					String sftpFileName = entry.getFilename();
					if (sftpFileName.equals(fileName)) {
						channel.rm(sftpPath + "/" + fileName);
					}
				}
			}
		} catch (Exception e) {
			log.error("sftp delete file failed ", e);
		}
	}
	
//	public static void main(String[] args) throws JSchException, SftpException {
//		
//
//		//ChannelSftp channel = getChannel("192.168.0.234", 7000, "sftpadmin", "Wzw_sftp_789!");
//
////
////		
////		
////		File file = new File("E:/test/sftp-test.txt" );
////		String sftpPath = "/incoming";
////		uploadFileToSftp(channel, sftpPath, file);
////		closeChannel(channel);
//
//		String sftpIP = "192.168.0.234";
//		String sftpPassWord = "Wzw_sftp_789!";
//		String sftpUserName = "sftpadmin";
//		int sftpPort = 7000;
//		
//		
//		JSch jsch = new JSch();
//		Session	session = jsch.getSession(sftpUserName, sftpIP, sftpPort);
//		System.out.println("Session created: " + session);
//		session.setPassword(sftpPassWord);			
//		Properties config = new Properties();
//		config.setProperty("StrictHostKeyChecking", "no");
//		session.setConfig(config);
//		session.connect();
//	
//		System.out.println("session connected");
//		System.out.println("opening channel");
//		for(int i=0; i<15; i++){
//			Channel	channel = session.openChannel("sftp");
//			channel.connect();
//			File file = new File("E:/test/sftp-test" + i + ".txt" );
//			String sftpPath = "/incoming";
//			((ChannelSftp) channel).put(file.getAbsolutePath(), sftpPath + "/" + file.getName(), ChannelSftp.OVERWRITE);//使用OVERWRITE模式
//			System.out.println("local file path " + file.getAbsolutePath());
//			System.out.println("remote sftp server path " + sftpPath);
//			System.out.println("==============" + i + "connected successfully to sftpHost = " + sftpIP + ", sftpUserName= " + sftpUserName + ", returning: " + channel);
//			//channel.disconnect();
//			//channel.getSession().disconnect();
//		}
//		
//		
//	}

}
