package com.reyhoo.minitalk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.reyhoo.minitalk.db.DBUtil;
import com.reyhoo.minitalk.db.TransationManager;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.exception.UserAlreadyExistException;
import com.reyhoo.minitalk.exception.UserNotFoundException;

public class UserDao {
	public static final String TABLE_NAME = "mt_user";

	private User findByMobile(String mobile) throws Exception {
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select * from "+TABLE_NAME+" where mobile=?");
			statement.setString(1, mobile);
			ResultSet rs = statement.executeQuery();
			User user = null;
			if (rs.next()) {
				int id = rs.getInt("id");
				String _mobile = rs.getString("mobile");
				String password = rs.getString("password");
				user = new User();
				user.setId(id);
				user.setMobile(_mobile);
				user.setPassword(password);
			}
			return user;
		} finally {
			// DBUtil.close();
		}
	}

	public Integer add(User u) throws Exception {
		try {
			TransationManager.beginTransation();
			Connection conn = DBUtil.getConnection();
			User user = findByMobile(u.getMobile());
			if (user != null) {
				throw new UserAlreadyExistException("该手机号已经被注册");
			}
			PreparedStatement statement = conn.prepareStatement("insert into "+TABLE_NAME+"(mobile,password,nickname)values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, u.getMobile());
			statement.setString(2, u.getPassword());
			statement.setString(3, "mt_" + u.getMobile());
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

	public static void main(String[] args) {
		try {
			User u = new User();
			u.setMobile("15129237121");
			u.setPassword("111111");
			int id = new UserDao().add(u);
			System.out.println(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User findUserForLogin(User u) throws Exception {
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select * from "+TABLE_NAME+" where mobile=? and password=?");
			statement.setString(1, u.getMobile());
			statement.setString(2, u.getPassword());
			ResultSet rs = statement.executeQuery();
			User user = null;
			if (rs.next()) {
				int id = rs.getInt("id");
				String mobile = rs.getString("mobile");
				String password = rs.getString("password");
				user = new User();
				user.setId(id);
				user.setMobile(mobile);
				user.setPassword(password);
			}
			if (user == null) {
				throw new UserNotFoundException("账号或密码错误");
			}
			return user;
		} finally {
			DBUtil.close();
		}
	}
}
