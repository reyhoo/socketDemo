package com.reyhoo.minitalk.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

	public static void handle(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		System.err.println(sw.toString());
	}
}
