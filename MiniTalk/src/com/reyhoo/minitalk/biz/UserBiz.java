package com.reyhoo.minitalk.biz;

import com.reyhoo.minitalk.dao.UserDao;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.exception.DataFormatException;
import com.reyhoo.minitalk.util.CommonUtil;

public class UserBiz {

	public User login(User u)throws Exception{
		if(CommonUtil.isEmptyString(u.getMobile())){
			throw new DataFormatException("手机号不能为空");
		}
		if(CommonUtil.isEmptyString(u.getPassword())){
			throw new DataFormatException("密码不能为空");
		}
		if(!CommonUtil.isMobile(u.getMobile())){
			throw new DataFormatException("手机号格式不正确");
		}
		return new UserDao().findUserForLogin(u);
	}
}
