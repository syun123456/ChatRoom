package com.syun.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FriendService {
	Set<String> getFriendList(String username);
	
	void setUserStatus(String username, String status);
	
	Map<String, String> getUserStatus(String username);
	
	void removeFriend(String username, String friend);
	
	Set<String> getRequestList(String username);
	
	void sendFriendRequest(String sender, String receiver);
	
	void acceptFriendRequest(String username, String sender);
	
	void removeFriendRequest(String username, String sender);
	
	Map<String, List<String>> getSuggestFriend(String username);
	
	public void saveMsg(String username, String friend, String msg);
	
	public List<String> getMsg(String username, String friend);
}
