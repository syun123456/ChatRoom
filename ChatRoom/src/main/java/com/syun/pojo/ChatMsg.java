package com.syun.pojo;

import java.io.Serializable;

public class ChatMsg implements Serializable{
	private int id;
	private String username;
	private String message;
	
	public ChatMsg() {
		
	}
	
	public ChatMsg(String username, String message) {
		this(0, username, message);
	}
	
	public ChatMsg(int id, String username, String message) {
		super();
		this.id = id;
		this.username = username;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ChatMsg [id=" + id + ", username=" + username + ", message=" + message + "]";
	}
	
}
