package com.reyhoo.minitalk.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	
	
	private static final ThreadLocal<Connection>conns = new ThreadLocal<>();
	
	public static Connection getConnection() throws Exception{
		Connection conn = conns.get();
		if(conn == null){
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/minitalk?useUnicode=true",
					"root","");
			conns.set(conn);
		}
		return conn;
		
	}
	
	public static void  close(){
		Connection conn = conns.get();
		conns.set(null);
		if(conn != null){
			close(conn);
		}
	}
	
	private static void close(Connection conn) {
		if(conn == null) return ;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println(getConnection());
			close();
			System.out.println(getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
}
