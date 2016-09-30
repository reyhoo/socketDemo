package com.reyhoo.minitalk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.reyhoo.minitalk.db.DBUtil;
import com.reyhoo.minitalk.db.TransationManager;
import com.reyhoo.minitalk.entity.Message;

public class MessageDao {
	public static final String TABLE_NAME = "mt_message";

	
	public void updateMsgStatus(int msgId,int status)throws Exception{
		try {
			TransationManager.beginTransation();
			Connection conn = DBUtil.getConnection();
			
			PreparedStatement statement = conn.prepareStatement("update " + TABLE_NAME + " set status=? where id=?");
			statement.setInt(1, status);
			statement.setInt(2, msgId);
			statement.executeUpdate();
			TransationManager.commit();
		} finally {
			TransationManager.endTransation();
		}
	}
	public Integer add(int fromId, int toId, String msgBody) throws Exception {
		try {
			TransationManager.beginTransation();
			Connection conn = DBUtil.getConnection();
			/**
			 * create table mt_message(id integer primary key auto_increment
			 * ,fromId integer,toId integer,msgBody varchar(255));
			 */
			PreparedStatement statement = conn.prepareStatement("insert into " + TABLE_NAME + "(fromId,toId,msgBody,time)values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, fromId);
			statement.setInt(2, toId);
			statement.setString(3, msgBody);
			statement.setLong(4, System.currentTimeMillis());
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			Integer id = null;
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			TransationManager.commit();
			return id;
		} finally {
			TransationManager.endTransation();
		}
	}

	public List<Message> getMessageByUserId(int userId, int currPage, int pageSize) throws Exception {
		if (currPage <= 0 || pageSize <= 0)
			return null;
		ArrayList<Message> list = new ArrayList<>();
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select * from " + TABLE_NAME + " where (toId=? and status=1) or fromId=? limit ?,?");
			statement.setInt(1, userId);
			statement.setInt(2, userId);
			statement.setInt(3, pageSize * (currPage-1));
			statement.setInt(4, pageSize);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				Message msg = new Message();
				msg.setId(rs.getInt("id"));
				msg.setFrom(rs.getInt("fromId"));
				msg.setTo(rs.getInt("toId"));
				msg.setMsgBody(rs.getString("msgBody"));
				msg.setTime(rs.getLong("time"));
				list.add(msg);
			}
		} finally {
			DBUtil.close();
		}
		return list;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(System.currentTimeMillis());
		MessageDao dao = new MessageDao();
		int count = 0;
		List<Message> list = dao.getMessageByUserId(0, 2, 1);
		System.out.println(list);
//		while (count < 100) {
//			String msgBody = "you are a rph " + (count + 1);
//			if (count % 3 == 0)
//				dao.add(1, 2, msgBody);
//			else {
//				if (count % 2 == 0)
//					dao.add(3, 2, msgBody);
//				else {
//					dao.add(2, 1, msgBody);
//				}
//
//			}
//			count++;
//		}
	}
}
