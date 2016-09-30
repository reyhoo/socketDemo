package com.reyhoo.talk.entity;

import java.io.Serializable;

public class User implements Serializable {

	private Integer id;
	private String mobile;
	private String password;
	
	private Boolean isOnline;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Boolean isOnline() {
		return isOnline;
	}
	public void setOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", mobile='" + mobile + '\'' +
				", password='" + password + '\'' +
				", isOnline=" + isOnline +
				'}';
	}
}
