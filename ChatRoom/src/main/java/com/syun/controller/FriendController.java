package com.syun.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syun.pojo.User;
import com.syun.service.FriendServiceImpl;
import com.syun.service.UserServiceImpl;

@Controller
public class FriendController {
	
	@Autowired
	private FriendServiceImpl friendService;
	
	@Autowired
	private UserServiceImpl userService;
	
	// 發送好友邀請
	@PostMapping("/friend/sendRequest")
	public String sendRequest(String receiver, HttpSession session, Model model, RedirectAttributes ra) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		// 判斷邀請對象是否存在
		if(!userService.usernameIsExist(receiver)) {
			ra.addFlashAttribute("msg", "該使用者不存在，請重新輸入");
		}else if(friendService.getFriendList(username).contains(receiver)){
			ra.addFlashAttribute("msg", "該使用者已經是您的好友");
		}else {
			friendService.sendFriendRequest(username, receiver);
			ra.addFlashAttribute("msg", "已向"+receiver+"申請好友");
		}
		
		return "redirect:/friend/list";
	}
	
	// 顯示好友名單
	@RequestMapping("/friend/list")
	public String getFriendList(HttpSession session, Model model, @ModelAttribute("msg") String msg) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		// 好友名單
		Set<String> friendList = friendService.getFriendList(username);
		// 好友上線/離線
		Map<String, String> friendStatus = friendService.getUserStatus(username);
		// 可能認識的好友
		Map<String, List<String>> suggestFriend = friendService.getSuggestFriend(username);
		
		model.addAttribute("friendList", friendList);
		model.addAttribute("friendStatus", friendStatus);
		model.addAttribute("suggestList", suggestFriend);
		return "friend/list";
	}
	
	// 取得所有的好友邀請
	@RequestMapping("/friend/request")
	public String getRequestList(HttpSession session, Model model) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		Set<String> requestList = friendService.getRequestList(username);
		
		if(requestList.size() == 0) {
			model.addAttribute("msg", "目前未有好友邀請");
		}
		
		model.addAttribute("requestList", requestList);
		
		return "friend/request";
	}
	
	// 接受好友邀請
	@RequestMapping("/friend/acceptRequest/{request}")
	public String acceptRequest(@PathVariable("request")String sender, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		friendService.acceptFriendRequest(username, sender);
		
		return "redirect:/friend/request";
	}
	
	// 拒絕好友邀請
	@RequestMapping("/friend/rejectRequest/{request}")
	public String rejectRequest(@PathVariable("request")String sender, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		friendService.removeFriendRequest(username, sender);
		
		return "redirect:/friend/request";
	}
	
	// 刪除好友
	@RequestMapping("/friend/remove/{friend}")
	public String removeFriend(@PathVariable("friend")String friend, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		friendService.removeFriend(username, friend);
		
		return "redirect:/friend/list";
	}
	
	// 搜尋其他使用者
	@PostMapping("/friend/search")
	@ResponseBody
	public String searchUserAjax(String receiver) throws JsonProcessingException{
		// 如果搜尋為空，則不取得list
		if("".equals(receiver)) {
			return "[]";
		}
		
		List<String> list = userService.searchUser(receiver);
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(list);
	}
	
	// 轉發至私訊頁面
	@RequestMapping("/friend/chat/{friend}")
	public String chat(@PathVariable("friend") String friend, HttpSession session, Model model, RedirectAttributes ra) {
		User user = (User) session.getAttribute("loginUser");
		String username = user.getUsername();
		
		if(!userService.usernameIsExist(friend)) {
			ra.addFlashAttribute("msg", "使用者"+friend+"不存在，請重新輸入");
			return "redirect:/friend/list";
		}
		
		if(!friendService.getFriendList(username).contains(friend)) {
			ra.addFlashAttribute("msg", "使用者"+friend+"非您的好友");
			return "redirect:/friend/list";
		}
		
		model.addAttribute("friendName", friend);
		
		return "friend/chat";
	}
}
