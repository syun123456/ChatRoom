package com.syun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService{
	
	@Autowired
	private RedisTemplate<String, String> redis;
	
	@Override
	public Set<String> getFriendList(String username) {
		Set<String> friendList = redis.opsForSet().members(username+":friendList");
		
		return friendList;
	}
	
	@Override
	public void setUserStatus(String username, String status) {
		redis.opsForHash().put("userStatus", username, status);
	}
	
	@Override
	public Map<String, String> getUserStatus(String username) {
		Map<String, String> map = new HashMap<String, String>();
		
		for(String friend:getFriendList(username)) {
			if(redis.opsForHash().hasKey("userStatus", friend)) {
				map.put(friend, (String) redis.opsForHash().get("userStatus", friend));
			}else {
				map.put(friend, "0");
			}
		}
		
		return map;
	}

	@Override
	public void removeFriend(String username, String friend) {
		redis.opsForSet().remove(username+":friendList", friend);
		redis.opsForSet().remove(friend+":friendList", username);
	}

	@Override
	public Set<String> getRequestList(String username) {
		Set<String> requestList = redis.opsForSet().members(username+":requestList");
		
		return requestList;
	}

	@Override
	public void sendFriendRequest(String sender, String receiver) {
		// 判斷發送者是否已為對方的好友
		if(!redis.opsForSet().isMember(receiver+":friendList", sender)) {
			// 判斷想加入的好友是否曾已發送過邀請
			if(getRequestList(sender).contains(receiver)) {
				acceptFriendRequest(sender, receiver);
			}else {
				redis.opsForSet().add(receiver+":requestList", sender);
			}
		}
	}

	@Override
	public void acceptFriendRequest(String username, String sender) {
		// 判斷同意加入的對象確實存在於自己的邀請名單
		if(redis.opsForSet().isMember(username+":requestList", sender)) {
			redis.opsForSet().add(username+":friendList", sender);
			redis.opsForSet().add(sender+":friendList", username);
		}
		removeFriendRequest(username, sender);
		}

	@Override
	public void removeFriendRequest(String username, String sender) {
		redis.opsForSet().remove(username+":requestList", sender);
	}
	
	@Override
	public  Map<String, List<String>> getSuggestFriend(String username){
		Set<String> set = redis.opsForSet().members(username+":friendList");
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		// 取得好友的好友
		for(String friend:set) {
			Set<String> temp = redis.opsForSet().difference(friend+":friendList", username+":friendList");
			for(String suggestFriend:temp) {
				if(suggestFriend.equals(username)) {
					continue;
				}
				
				if(!map.containsKey(suggestFriend)) {
					map.put(suggestFriend, new ArrayList<String>());
				}
				map.get(suggestFriend).add(friend);
			}
		}
		
		return map;
	}
	
	// 保存聊天紀錄
	@Override
	public void saveMsg(String username, String friend, String msg) {
		if(redis.hasKey(friend+":chat:"+username)) {
			redis.opsForList().rightPush(friend+":chat:"+username, msg);
		}else {
			redis.opsForList().rightPush(username+":chat:"+friend, msg);
		}
	}
	
	@Override
	public List<String> getMsg(String username, String friend){
		if(redis.hasKey(username+":chat:"+friend)) return redis.opsForList().range(username+":chat:"+friend, 0, -1);
		
		if(redis.hasKey(friend+":chat:"+username)) return redis.opsForList().range(friend+":chat:"+username, 0, -1);
		
		return null;
	}

}
