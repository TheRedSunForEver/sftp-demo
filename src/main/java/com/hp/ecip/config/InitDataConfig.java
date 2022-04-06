package com.hp.ecip.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hp.ecip.service.UserService;

@Component
public class InitDataConfig implements CommandLineRunner{
	
	private static Logger log = LoggerFactory.getLogger(InitDataConfig.class);
	
	@Value("${sftp.instance_id}")
	private String instanceId;
	
	@Autowired
	private UserService userService;

	@Override
	public void run(String... args) throws Exception {
		log.info("====InitDataConfig run begin...");
		log.info("====InitDataConfig run instanceId: " + instanceId);
		boolean status = userService.setUserAuthAndRoute(instanceId);
		log.info("InitDataConfig run load user and route path status: " + status);
	}

}
