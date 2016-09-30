package com.reyhoo.talk.component;

import com.google.gson.Gson;

public class Resp<T> {

	public Integer requestId;
	public String type;
	public String errCode;
	public String errMsg;
	public T content;


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public boolean isSuccess(){
		if("0000".equals(errCode)){
			return true;
		}
		return false;
	}
}
