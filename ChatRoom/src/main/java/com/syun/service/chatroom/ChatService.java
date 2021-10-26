package com.syun.service.chatroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.syun.pojo.ChatMsg;
import com.syun.pojo.ChatRoom;

@Service
public class ChatService {
	private Map<String, ChatRoom> chatRoomMap = new HashMap<>();     // 房名與ChatRoom物件映射
	private Map<String, ChatServer> chatServerMap = new HashMap<>(); // 房名與ChatServer物件映射
	private Map<String, List<ChatClient>> chatClientMap = new HashMap<>();  // 使用者擁有的ChatClient物件映射
	private List<String[]> chatroomList = new ArrayList<>(); // 聊天室列表
	private Map<String, List<String[]>> inviteInfoMap = new HashMap<>(); // Map<被邀請人, List<[房名, 類型, 邀請人]>>
	
	// 建立ChatServer
	public void creatChatServer(String chatroomName, String chatroomType, String chatroomPassword) {
		// 建立聊天室相關資訊
		ChatRoom chatRoom = new ChatRoom(chatroomName, chatroomType, chatroomPassword);
		
		// 建立新的ChatServer
		ChatServer server = new ChatServer();
		
		this.addChatServer(chatRoom, server);
	}
	
	// 取得聊天室相對應的Server
	public ChatServer getChatServer(String chatroomName) {
		return chatServerMap.get(chatroomName);
	}
	
	// 取得聊天室列表
	public List<String[]> getChatroomList() {
		return chatroomList;
	}
	
	// 判斷房名是否重複
	public boolean chatroomNameIsExist(String chatroomName) {
		return chatRoomMap.get(chatroomName) != null;
	}
	
	// 判斷房名與密碼是否一致
	public boolean chatroomPasswordIsCorrect(String chatroomName, String chatroomPassword) {
		return chatRoomMap.get(chatroomName).getChatroomPassword().equals(chatroomPassword);
	}
	
	// 當ChatServer建立時，放入相關資訊
	public void addChatServer(ChatRoom chatRoom, ChatServer server) {
		chatRoomMap.put(chatRoom.getChatroomName(), chatRoom);
		chatServerMap.put(chatRoom.getChatroomName(), server);
		// 僅放入房名及類型資訊返回給前端
		String[] str = {chatRoom.getChatroomName(), chatRoom.getChatroomType()};
		chatroomList.add(str);
	}
	
	// 移除ChatServer及其相關資訊
	public void removeChatServer(String chatroomName, ChatServer server) {
		chatRoomMap.remove(chatroomName);
		chatServerMap.remove(chatroomName);
		for(int i = 0; i < chatroomList.size(); i++) {
			String[] temp = chatroomList.get(i);
			if(temp[0].equals(chatroomName)) {
				chatroomList.remove(i);
				break;
			}
		}
		server = null;
	}
	
	// 建立ChatClient
	public void createChatClient(String chatroomName, String username) {
		ChatServer server = chatServerMap.get(chatroomName);
		
		ChatClient client = new ChatClient(chatroomName, username, server, this);
		
		server.addChatClient(chatroomName, username, client);
		
		this.addChatClient(username, client);
	}
	
	// 當ChatClient建立時，放入相關資訊
	public void addChatClient(String username, ChatClient client) {
		if(!chatClientMap.containsKey(username)) {
			List<ChatClient> clientList = new ArrayList<>();
			clientList.add(client);
			chatClientMap.put(username, clientList);
		}else {
			chatClientMap.get(username).add(client);
		}
	}
	
	// 取得使用者相對應的client端
	public ChatClient getChatClient(String chatroomName, String username) {
		if(!chatClientMap.containsKey(username)) {
			return null;
		}
		
		List<ChatClient> clientList = chatClientMap.get(username);
		for(int i = 0; i < clientList.size(); i++) {
			String clientChatroomName = clientList.get(i).getChatroomName();
			if(chatroomName.equals(clientChatroomName)) {
				return clientList.get(i);
			}
		}
		
		return null;
	}
	
	// 移除ChatClient及其相關資訊
	public void removeChatClient(String chatroomName, String username) {
		ChatServer server = chatServerMap.get(chatroomName);
		
		server.removeChatClient(username);
		
		// 如果聊天室的使用者都離開，則關閉聊天室
		if(server.getUsernames().size() == 0) {
			this.removeChatServer(chatroomName, server);
		}
		
		List<ChatClient> clientList = chatClientMap.get(username);
		for(int i = 0; i < clientList.size(); i++) {
			String clientChatroomName = clientList.get(i).getChatroomName();
			if(chatroomName.equals(clientChatroomName)) {
				chatClientMap.get(username).remove(i);
				return;
			}
		}
	}
	
	public Set<String> getUsernames(String chatroomName){
		return this.getChatServer(chatroomName).getUsernames();
	}
	
	// 放入邀請資訊
	public void addInviteInfo(String inviteUser, String chatroomName, String username) {
		String[] inviteInfo = {chatRoomMap.get(chatroomName).getChatroomName(), chatRoomMap.get(chatroomName).getChatroomType(), username};
		
		if(!inviteInfoMap.containsKey(inviteUser)) {
			List<String[]> inviteInfoList = new ArrayList<>();
			inviteInfoList.add(inviteInfo);
			inviteInfoMap.put(inviteUser, inviteInfoList);
		}else {
			List<String[]> inviteInfoList = inviteInfoMap.get(inviteUser);
			// 判斷邀請加入的聊天室是否重複，有重複則更新邀請人
			for(int i = 0; i < inviteInfoList.size(); i++) {
				String temp = inviteInfoList.get(i)[0]; // 從list中取聊天室名稱
				if(temp.equals(chatroomName)) {
					inviteInfoList.get(i)[2] = username;
					return;
				}
			}
			inviteInfoMap.get(inviteUser).add(inviteInfo);
		}
	}
	
	// 取得邀請資訊
	public List<String[]> getInviteInfo(String username) {
		return inviteInfoMap.get(username);
	}
	
	// 移除邀請資訊
	public void removeInviteInfo(String chatroomName, String username) {
		if(!inviteInfoMap.containsKey(username)) {
			return;
		}
		
		List<String[]> inviteInfoList = inviteInfoMap.get(username);
		for(int i = 0; i < inviteInfoList.size(); i++) {
			String temp = inviteInfoList.get(i)[0];
			if(chatroomName.equals(temp)) {
				inviteInfoMap.get(username).remove(i);
			}
		}
	}
	
	// 使用者發送訊息
	public void sendMsg(String chatroomName, String username, String inputMsg) {
		// 調用相對應的client，並發送訊息到server
		this.getChatClient(chatroomName, username).sendMessage(inputMsg);
	}
	
	// 使用者讀取訊息
	public ChatRoom getMsg(String chatroomName, String username, int id) {
		List<ChatMsg> chatMsgList = this.getChatClient(chatroomName, username).getChatMsgList(id);
		List<ChatMsg> tempList = new ArrayList<>();
		
		// id為使用者已讀取的訊息位置
		for(int i = id; i < chatMsgList.size(); i++) {
			tempList.add(chatMsgList.get(i));
		}
		
		Set<String> users = this.getChatServer(chatroomName).getUsernames();
		
		ChatRoom chatRoom = new ChatRoom(users, tempList);
		
		return chatRoom;
	}
	
	// 傳入使用者網頁是否存活
	public void setClientAlive(String chatroomName, String username, String alive) {
		this.getChatClient(chatroomName, username).setAlive(alive);
	}
}
