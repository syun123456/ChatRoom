package com.syun.service.chatroom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatServer extends Thread{
	private Set<String> usernames = new HashSet<>(); // 存放同一個聊天室的使用者
	private Set<ChatClient> clients = new HashSet<>(); // 存放同一個聊天室的ChatClient物件
	private Map<String, ChatClient> userClientMap = new HashMap<>(); // 使用者名稱與ChatClient映射
	
	// Server向所有Client端的使用者發送訊息
	synchronized void sendMessageToAll(String message) {
		for(ChatClient client:clients) {
			client.setChatMsg(message);
		}
	}
	
	public Set<String> getUsernames(){
		return this.usernames;
	}
	
	public void addChatClient(String chatroomName, String username, ChatClient client) {
		usernames.add(username);
		clients.add(client);
		userClientMap.put(username, client);
		this.sendMessageToAll(username + "已加入聊天室!");
	}
	
	public void removeChatClient(String username) {
		ChatClient client = userClientMap.get(username);
		client.timerCancel();
		usernames.remove(username);
		clients.remove(client);
		userClientMap.remove(username);
		client = null;
		this.sendMessageToAll(username + "已離開聊天室!");
	}
	
}
