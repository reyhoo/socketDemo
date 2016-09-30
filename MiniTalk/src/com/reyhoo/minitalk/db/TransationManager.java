package com.reyhoo.minitalk.db;

import java.sql.Connection;

public class TransationManager {

	
	public static void beginTransation()throws Exception{
		Connection conn = DBUtil.getConnection();
		conn.setAutoCommit(false);
	}
	
	public static void rollback()throws Exception{
		Connection conn = DBUtil.getConnection();
		conn.rollback();
	}
	public static void commit()throws Exception{
		Connection conn = DBUtil.getConnection();
		conn.commit();
	}
	public static void endTransation(){
		DBUtil.close();
	}
}
