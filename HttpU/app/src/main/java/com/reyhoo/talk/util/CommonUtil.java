package com.reyhoo.talk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

	
	public static final String getCurrTime(){
		try {
			
			Date date = new Date();
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static final String MOBILE_REG = "^[1][34578][0-9]{9}$";
	
	public static boolean isMobile(String mobile){
		return mobile.matches(MOBILE_REG);
	}
	
	public static boolean isEmptyString(String s){
		if(s == null||"".equals(s.trim()))
			return true;
		return false;
	}
}
