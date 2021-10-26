package com.syun.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ChatRoom implements Serializable{
	private String chatroomName;
	private String chatroomType;
	private String chatroomPassword;
	private Set<String> users; 
	private List<ChatMsg> chatMsgList;
	
	public ChatRoom() {
		
	}
	
	public ChatRoom(String chatroomName, String chatroomType, String chatroomPassword) {
		super();
		this.chatroomName = chatroomName;
		this.chatroomType = chatroomType;
		this.chatroomPassword = chatroomPassword;
	}
	
	public ChatRoom(Set<String> users, List<ChatMsg> chatMsgList) {
		super();
		this.users = users;
		this.chatMsgList = chatMsgList;
	}
	public String getChatroomName() {
		return chatroomName;
	}
	public void setChatroomName(String chatroomName) {
		this.chatroomName = chatroomName;
	}
	public String getChatroomType() {
		return chatroomType;
	}
	public void setChatroomType(String chatroomType) {
		this.chatroomType = chatroomType;
	}
	public String getChatroomPassword() {
		return chatroomPassword;
	}
	public void setChatroomPassword(String chatroomPassword) {
		this.chatroomPassword = chatroomPassword;
	}
	public Set<String> getUsers() {
		return users;
	}
	public void setUsers(Set<String> users) {
		this.users = users;
	}
	public List<ChatMsg> getChatMsgList() {
		return chatMsgList;
	}
	public void setChatMsgList(List<ChatMsg> chatMsgList) {
		this.chatMsgList = chatMsgList;
	}
	@Override
	public String toString() {
		return "ChatRoom [chatroomName=" + chatroomName + ", chatroomType=" + chatroomType + ", chatroomPassword="
				+ chatroomPassword + ", users=" + users + ", chatMsgList=" + chatMsgList + "]";
	}	

}
