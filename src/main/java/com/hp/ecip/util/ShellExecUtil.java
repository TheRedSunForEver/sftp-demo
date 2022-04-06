package com.hp.ecip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellExecUtil {
	
	private static Logger log = LoggerFactory.getLogger(ShellExecUtil.class);
	
	public static String execShell(String shellPath) throws IOException, InterruptedException{
		String result = "";
		//try {
			Process ps = Runtime.getRuntime().exec(shellPath);
			ps.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while((line = br.readLine()) != null){
				sb.append(line).append("\n");
			}
			result = sb.toString();
//
		return result;
	}
	
	
	public static String excuteShellCmd(String[] cmdarray){
		Process p = null;
		BufferedReader br = null;
		String result = "";
		try {
			p = Runtime.getRuntime().exec(cmdarray);
			p.waitFor();
			br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
			String line = "";
			while((line  = br.readLine()) != null){
				result += line;
			}
		} catch (Exception e) {
			log.error("excute shell error " , e);
			result = e.getMessage();
		}finally{			
			try {
				if(br != null){
					br.close();
				}
				if(p != null){
					p.destroy();
				}				
			} catch (Exception e2) {
				log.error("shell close error " , e2);
			}
		}		
		return result;
	}
}
