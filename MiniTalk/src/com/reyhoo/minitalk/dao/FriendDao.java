package com.reyhoo.minitalk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.reyhoo.minitalk.component.TalkServer;
import com.reyhoo.minitalk.db.DBUtil;
import com.reyhoo.minitalk.db.TransationManager;
import com.reyhoo.minitalk.entity.Friend;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.exception.BusniessException;

public class FriendDao {

	public static final String TABLE_NAME = "mt_friend";

	public List<Friend> getFriendsByOwnerId(int ownerId) throws Exception {
		ArrayList<Friend> list = new ArrayList<>();
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select f.id as fid,f.ownerId,f.userId,f.remarks,u.* from" + " " + TABLE_NAME + " f right join " + UserDao.TABLE_NAME
					+ " u on f.userId=u.id where f.ownerId=?");
			statement.setInt(1, ownerId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Friend f = new Friend();
				f.setId(rs.getInt("fid"));
				f.setOwnerId(rs.getInt("ownerId"));
				f.setRemarks(rs.getString("remarks"));
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setMobile(rs.getString("mobile"));
				user.setNickname(rs.getString("nickname"));
				user.setOnline(TalkServer.getInstance().containUser(user));
				f.setUser(user);
				list.add(f);
			}
		} finally {
			DBUtil.close();
		}
		return list;
	}

	public Integer add(int ownerId, User u) throws Exception {

		try {
			TransationManager.beginTransation();
			boolean exists = getFriendForUnique(ownerId, u.getId());
			if(exists){
				throw new BusniessException("不能重复添加好友");
			}
			/**
			 * insert into mt_friend(ownerId,userId,remarks)values(1,11,'m');
			 */
			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("insert into " + TABLE_NAME + "(ownerId,userId,remarks)values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, ownerId);
			statement.setInt(2, u.getId());
			statement.setString(3, u.getNickname());
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

	private boolean getFriendForUnique(int ownerId, int userId) throws Exception {
		ResultSet rs = null;
		try {

			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select * from " + TABLE_NAME + " where ownerId=? and userId=?");
			statement.setInt(1, ownerId);
			statement.setInt(2, userId);
			rs = statement.executeQuery();
			return rs.next();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		User u = new User();
		u.setId(2);
		u.setNickname("hahah");
		System.out.println(new FriendDao().add(1, u ));
	}
}
