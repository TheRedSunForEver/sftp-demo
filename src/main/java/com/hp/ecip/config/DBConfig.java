package com.hp.ecip.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class DBConfig {
	
	private static Logger log = LoggerFactory.getLogger(DBConfig.class);
	
//	@Value("${spring.datasource.url}")
//	private String url;
//	
//	@Value("${spring.datasource.username}")
//	private String userName;
//	
//	@Value("${spring.datasource.password}")
//	private String password;
//	
//	@Value("${spring.datasource.driver-class-name}")
//	private String driverClassName;
//	
//	@Value("${spring.datasource.maxIdleTime}")
//	private String maxIdleTime;
//	
//	@Value("${spring.datasource.idleConnectionTestPeriod}")
//	private String testPeriod;
//	
//	@Value("${spring.datasource.preferredTestQuery}")
//	private String testQuery;
	
	@Value("${c3p0.oracle.url}")
	private String url;
	
	@Value("${c3p0.oracle.username}")
	private String userName;
	
	@Value("${c3p0.oracle.password}")
	private String password;
	
	@Value("${c3p0.oracle.driver-class-name}")
	private String driverClassName;
	
	@Value("${c3p0.oracle.maxIdleTime}")
	private String maxIdleTime;
	
	@Value("${c3p0.oracle.idleConnectionTestPeriod}")
	private String testPeriod;
	
	@Value("${c3p0.oracle.preferredTestQuery}")
	private String testQuery;
	
	@Bean
	@Primary
	public DataSource dataSource(){
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(driverClassName);
			dataSource.setJdbcUrl(url);
			dataSource.setUser(userName);
			byte[] bytes = Base64.decode(password.getBytes());
			String decryptPassword = new String(bytes);
			dataSource.setPassword(decryptPassword);
			dataSource.setMaxIdleTime(Integer.valueOf(maxIdleTime));
			dataSource.setIdleConnectionTestPeriod(Integer.valueOf(testPeriod));
			dataSource.setPreferredTestQuery(testQuery);
		} catch (PropertyVetoException e) {
			log.error("注入c3p0发生异常", e);
		}
		return dataSource;
	}
	
}
