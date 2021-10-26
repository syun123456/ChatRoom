package com.syun.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Component
@ServerEndpoint("/WebSocket/{username}/{friend}")
public class WebSocketService {
	
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
	    return new ServerEndpointExporter();
	}
	
	private static FriendService friendService;
	
	@Autowired
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	
	private static Map<String, Set<Session>> map = new ConcurrentHashMap<String, Set<Session>>();
	private String username;
	private String friend;
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		String msg = this.username + ":" + message;
		this.sendMsg(msg);
		
		friendService.saveMsg(username, friend, msg);
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("friend") String friend) throws IOException {
		this.username = username;
		this.friend = friend;
		this.addSession(session);
		
		List<String> msgList = friendService.getMsg(username, friend);
		
		if(msgList != null) {
			for(String msg:msgList) {
				this.sendMsg(msg);
			}
		}
		
	}
	
	@OnClose
    public void onClose(Session session) {
        this.removeSession(session);
    }
  
    @OnError
    public void onError(Session session, Throwable t) {
    	
    }
    
    private void addSession(Session session) {	
    	if(this.getSessionSet(username, friend) == null) {
			map.put(username+":"+friend, new CopyOnWriteArraySet<Session>());
		}
		
		map.get(username+":"+friend).add(session);
    }
    
    private void sendMsg(String message) throws IOException {
    	if(this.getSessionSet(username, friend) != null) {
    		for(Session session:this.getSessionSet(username, friend)) {
    			session.getBasicRemote().sendText(message);
    		}
    	}
    	
    	if(this.getSessionSet(friend, username) != null) {
    		for(Session session:this.getSessionSet(friend, username)) {
    			session.getBasicRemote().sendText(message);
    		}
    	}
    }
    
    private Set<Session> getSessionSet(String username, String friend) {
    	if(!map.containsKey(username+":"+friend)) {
    		return null;
    	}else {
    		return map.get(username+":"+friend);
    	}
    }
    
    private void removeSession(Session session) {
    	this.getSessionSet(username, friend).remove(session);
    }
    
}
