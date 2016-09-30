package com.reyhoo.minitalk;

import com.reyhoo.minitalk.component.TalkServer;
import com.reyhoo.minitalk.util.Md5Util;

public class Main {

	public static void main(String[] args) {
		try {
			TalkServer.getInstance().startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		String test = "{\"type\":\"1\",\"content\":{\"mobile\":\"12121212\",\"password\":\"dddddd\"}}";
//		Req req = (Req)new Gson().fromJson(test,Req.class);
//		System.out.println(req.content);
//		try {
//			String line = null;
//			line.length();
//		} catch (Exception e) {
//			ExceptionUtil.handle(new Exception("kkkjjk", e));
//		}
//		System.out.println(Md5Util.getMD5Str("222222"));
//		P p = new C();
//		p.hello();
		
	}
}
