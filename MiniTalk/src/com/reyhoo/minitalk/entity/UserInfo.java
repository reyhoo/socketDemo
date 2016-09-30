package com.reyhoo.minitalk.entity;

import java.util.HashSet;
import java.util.Set;

public class UserInfo {

	
	private User user;
	private Set<Friend> friends = new HashSet<Friend>();

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void addFriend(Friend u) {
		if (friends != null) {
			friends.add(u);
		}
	}

	public void setFriends(Set<Friend> friends) {
		this.friends = friends;
	}

	public Set<Friend> getFriends() {
		return friends;
	}
}
