package com.syun.service.chatroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.syun.pojo.ChatMsg;

// 使用者發送訊息及讀取其他使用者訊息
public class ChatClient{
	private String chatroomName;
	private String username;
	private ChatServer server;
	private ChatService service;
	private int id = 0;
	private String clientMessage;
	private ChatMsg chatMsg;
	List<ChatMsg> chatMsgList = new ArrayList<ChatMsg>();
	private String alive = "0";
	private Timer timer = new Timer();

	public ChatClient(String chatroomName, String username, ChatServer server, ChatService service) {
		this.chatroomName = chatroomName;
		this.username = username;
		this.server = server;
		this.service = service;
		this.ack();
	}
	
	public void sendMessage(String message) {
		clientMessage = username + ":" + message;
		server.sendMessageToAll(clientMessage);
	}
	
	public synchronized void setChatMsg(String message) {
		if(message != null) {
			// server自動發送的訊息不會有":"
			if(!message.contains(":")) {
				chatMsg = new ChatMsg(id, "server", message);
			}else {
				String[] chatMsgArr = message.split(":", 2);
				chatMsg = new ChatMsg(id, chatMsgArr[0], chatMsgArr[1]);
			}
		}

		// 將資訊放入chatMsgList中
		chatMsgList.add(chatMsg);
		id++;
		this.notifyAll();
	}
	
	public synchronized List<ChatMsg> getChatMsgList(int idIsRead){
		if(chatMsgList == null || idIsRead == chatMsgList.size()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return chatMsgList;
	}
	
	public String getChatroomName() {
		return chatroomName;
	}
	
	// 每隔十分鐘確認使用者網頁是否存活
	public void ack() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// "1"表示網頁存活，透過ajax傳入alive數值
				if("1".equals(alive)) {
					alive = "0";
					ack();
				}else {
					service.removeChatClient(chatroomName, username);
				}
			}
		};
		timer.schedule(task, 600000);
	}
	
	public void setAlive(String alive) {
		this.alive = alive;
	}
	
	public void timerCancel() {
		timer.cancel();
	}
}
