package com.reyhoo.talk.entity;

import com.google.gson.Gson;

import java.io.Serializable;

public class Message implements Serializable{

	private Integer id;
	private Integer from;
	private Integer to;
	private String msgBody;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFrom() {
		return from;
	}
	public void setFrom(Integer from) {
		this.from = from;
	}
	public Integer getTo() {
		return to;
	}
	public void setTo(Integer to) {
		this.to = to;
	}
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
