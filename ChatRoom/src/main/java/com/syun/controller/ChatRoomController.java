package com.syun.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syun.pojo.User;
import com.syun.service.UserServiceImpl;
import com.syun.service.chatroom.ChatService;

@Controller
public class ChatRoomController {
	
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private ChatService chatService;
	
	// 轉發到聊天室建立頁面
	@GetMapping("/chatroom/add")
	public String toAddPage() {
		return "chatroom/add";
	}
	
	// 建立聊天室
	@PostMapping("/chatroom/add")
	public String add(String chatroomName, String chatroomType, String chatroomPassword, Model model) throws InterruptedException {
		// 判斷房名是否重複
		if(chatService.chatroomNameIsExist(chatroomName)) {
			model.addAttribute("msg", "房名已被使用，請重新輸入!");
			return "chatroom/add";
		}
		
		// 建立新的聊天室
		chatService.creatChatServer(chatroomName, chatroomType, chatroomPassword);
		
		// ChatServer建立完成跳到聊天室頁面
		return "redirect:/chatroom/join/" + chatroomName;
	}
	
	// 顯示目前存在的聊天室
	@RequestMapping("/chatroom/list")
	public String getList(Model model){
		model.addAttribute("chatroomList", chatService.getChatroomList());
		return "chatroom/list";
	}
	
	// 如為私人聊天室則轉發到驗證頁面
	@GetMapping("/chatroom/confirm/{chatroomName}")
	public String toConfirmPage(@PathVariable("chatroomName") String chatroomName, Model model) {
		// 如聊天室不存在，則返回聊天室列表
		if(chatService.getChatServer(chatroomName) == null) {
			model.addAttribute("chatroomList", chatService.getChatroomList());
			model.addAttribute("msg", "指定的聊天室不存在或已關閉，請重新選取");
			return "chatroom/list";
		}
		
		model.addAttribute("chatroomName", chatroomName);
		return "chatroom/confirm";
	}
	
	// 驗證私人聊天室的密碼
	@PostMapping("/chatroom/confirm")
	public String confirm(String chatroomName, String chatroomPassword, Model model) {
		if(!chatService.chatroomPasswordIsCorrect(chatroomName, chatroomPassword)) {
			model.addAttribute("chatroomName", chatroomName);
			model.addAttribute("msg", "密碼錯誤");
			return "chatroom/confirm";
		}
		
		return "redirect:/chatroom/join/" + chatroomName;
	}
	
	// 請求加入聊天室，並新建一個Client
	@RequestMapping("/chatroom/join/{chatroomName}")
	public String join(@PathVariable("chatroomName") String chatroomName, HttpSession session, Model model) {
		// 如聊天室不存在，則返回聊天室列表
		if(chatService.getChatServer(chatroomName) == null) {
			model.addAttribute("chatroomList", chatService.getChatroomList());
			model.addAttribute("msg", "指定的聊天室不存在或已關閉，請重新選取");
			return "chatroom/list";
		}
		
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		// 將房名返回給前端
		model.addAttribute("chatroomName", chatroomName);
		
		// 判斷Client是否已存在
		if(chatService.getChatClient(chatroomName, username) != null) {
			return "chatroom/chatroom";
		}
		
		// 如透過邀請加入，則加入後刪除邀請資訊
		chatService.removeInviteInfo(chatroomName, username);
		
		chatService.createChatClient(chatroomName, username);
		
		return "chatroom/chatroom";
	}
	
	// 使用者發送訊息
	@PostMapping("/chatroom/sendMsg")
	@ResponseBody
	public String sendMsgAjax(String chatroomName, String inputMsg, HttpSession session) throws JsonProcessingException{
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		// json轉換類
		ObjectMapper mapper = new ObjectMapper();
		
		// 判斷ChatServer或ChatClient是否存在
		if(chatService.getChatServer(chatroomName) == null || chatService.getChatClient(chatroomName, username) == null) {
			return mapper.writeValueAsString(null);
		}
		
		chatService.sendMsg(chatroomName, username, inputMsg);

		return mapper.writeValueAsString("OK");
	}
	
	// 使用者接收其他人發送的訊息
	@PostMapping("/chatroom/getMsg")
	@ResponseBody
	public String getMsgAjax(String chatroomName, String username, int id) throws JsonProcessingException{	
		// json轉換類
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(chatService.getMsg(chatroomName, username, id));
	}
	
	// 邀請其他使用者加入聊天室
	@PostMapping("/chatroom/invite")
	@ResponseBody
	public String inviteAjax(String inviteUser, String chatroomName, String username) {
		// 如聊天室不存在或發送邀請者不在聊天室則返回錯誤
		if(chatService.getChatServer(chatroomName) == null || chatService.getChatClient(chatroomName, username) == null) {
			return "error";
		}
		
		Set<String> users = chatService.getUsernames(chatroomName);
		
		if(users.contains(inviteUser)) {
			return "使用者" + inviteUser + "已在聊天室";
		}
		
		if(userService.queryUserByUsername(inviteUser) == null) {
			return "該使用者不存在，請重新輸入";
		}
		
		chatService.addInviteInfo(inviteUser, chatroomName, username);
		
		return "您已向" + inviteUser + "發送邀請";
	}
		
	// 取得邀請通知
	@RequestMapping("/chatroom/inviteInfo")
	public String getInviteInfo(HttpSession session, Model model) throws JsonProcessingException {
		User user = (User) session.getAttribute("loginUser");
		
		List<String[]> inviteInfoList = chatService.getInviteInfo(user.getUsername());
		
		if(inviteInfoList == null || inviteInfoList.size() == 0) {
			model.addAttribute("msg", "目前未有邀請");
		}else {
			model.addAttribute("msg", "目前共有" + inviteInfoList.size() + "個邀請");
		}
		
		model.addAttribute("inviteInfoList", inviteInfoList);
		
		return "chatroom/inviteInfo";
	}
		
	// 移除邀請通知
	@RequestMapping("/chatroom/inviteInfo/remove/{chatroomName}")
	public String removeInviteInfo(@PathVariable("chatroomName") String chatroomName, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		
		chatService.removeInviteInfo(chatroomName, user.getUsername());
		
		return "redirect:/chatroom/inviteInfo";
	}
	
	// 離開聊天室
	@PostMapping("/chatroom/leave")
	public String leave(@RequestParam("leaveChatroom")String chatroomName, @RequestParam("leaveUser") String username) {
		if(chatService.getChatServer(chatroomName) == null || chatService.getChatClient(chatroomName, username) == null) {
			return "redirect:/user/main";
		}
		
		chatService.removeChatClient(chatroomName, username);
		
		return "redirect:/user/main";
	}
	
	@PostMapping("/chatroom/add/ajax")
	@ResponseBody
	public String addAjax(String chatroomName) {
		if(chatService.chatroomNameIsExist(chatroomName)) {
			return "房名已被使用，請重新輸入!";
		}
		
		return null;
	}
	
	// 確認使用者網頁是否還開啟
	@PostMapping("/chatroom/alive")
	@ResponseBody
	public String setAliveAjax(String chatroomName, String username, String alive) {
		if(chatService.getChatClient(chatroomName, username) == null) {
			return null;
		}
		chatService.setClientAlive(chatroomName, username, alive);
		
		return "OK";
	}
	
}
