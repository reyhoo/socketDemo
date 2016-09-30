package com.reyhoo.minitalk.component;

import java.util.Properties;

import com.reyhoo.minitalk.util.ExceptionUtil;

public class GlobalConfig {

	
	public static final int serverPort;
	static{
		Properties properties = new Properties();
		int port = 10000;
		try {
			properties.load(GlobalConfig.class.getResourceAsStream("/config.properties"));
			port = Integer.parseInt((String) properties.get("serverPort"));
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		serverPort = port;
	}
}
