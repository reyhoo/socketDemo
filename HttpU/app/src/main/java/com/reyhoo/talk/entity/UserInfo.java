package com.reyhoo.talk.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserInfo implements Serializable {

	private User user;
	private Set<User> friends = new HashSet<User>();

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void addFriend(User u) {
		if (friends != null) {
			friends.add(u);
		}
	}

	public void setFriends(Set<User> friends) {
		this.friends = friends;
	}

	public Set<User> getFriends() {
		return friends;
	}
}
