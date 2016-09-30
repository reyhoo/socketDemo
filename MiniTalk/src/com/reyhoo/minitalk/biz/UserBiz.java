package com.reyhoo.minitalk.biz;

import com.reyhoo.minitalk.dao.UserDao;
import com.reyhoo.minitalk.entity.User;
import com.reyhoo.minitalk.exception.DataFormatException;
import com.reyhoo.minitalk.util.CommonUtil;

public class UserBiz {

	public User login(User u)throws Exception{
		if(CommonUtil.isEmptyString(u.getMobile())){
			throw new DataFormatException("�ֻ��Ų���Ϊ��");
		}
		if(CommonUtil.isEmptyString(u.getPassword())){
			throw new DataFormatException("���벻��Ϊ��");
		}
		if(!CommonUtil.isMobile(u.getMobile())){
			throw new DataFormatException("�ֻ��Ÿ�ʽ����ȷ");
		}
		return new UserDao().findUserForLogin(u);
	}
}
