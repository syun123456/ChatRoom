package com.syun.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.syun.pojo.User;
import com.syun.service.FriendServiceImpl;
import com.syun.service.UserServiceImpl;

@Controller
public class LoginController {
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private FriendServiceImpl friendService;
	
	// 登入頁面
	@GetMapping("/login")
	public String loginPage(HttpServletRequest request, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		
		// 如果已登入，且未登出，請求登入頁面時直接前往首頁
		if(user != null) {
			if("admin".equals(user.getUsername())) {
				return "redirect:/admin/main";
			}else {
				friendService.setUserStatus(user.getUsername(), "1");
				return "redirect:/user/main"; // 重定向的網頁只能放在static資料夾下，或是在config類中配置
			}
		}
		
		// 如果有勾選rememberMe則直接前往首頁
		Cookie[] cookies = request.getCookies();

		if(cookies != null) {
			for(int i = 0; i < cookies.length; i++) {
				if("rememberMe".equals(cookies[i].getName())) {
					user = userService.queryUserByUsername(cookies[i].getValue());
					session.setAttribute("loginUser", user);
					if("admin".equals(user.getUsername())) {
						return "redirect:/admin/main";
					}else {
						friendService.setUserStatus(user.getUsername(), "1");
						return "redirect:/user/main";
					}
				}
			}
		}
		
		return "login";
	}
	
	// 登入資訊驗證
	@PostMapping("/login")
	public String login(String username,String password, String rememberMe, Model model, HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException {
		User user = userService.queryUserByUsername(username);
		
		if("yes".equals(rememberMe)) {
			Cookie cookie = new Cookie("rememberMe", username);
			cookie.setMaxAge(7 * 24 * 60 * 60); // 設定cookie存活時間(7天)，如未設定則存活至session消失
			cookie.setHttpOnly(true);
			response.addCookie(cookie);
		}
		
		if(userService.usernameIsExist(username) && userService.passwordIsCorrect(username, password)) {
			session.setAttribute("loginUser", user);
			if("admin".equals(username)) {
				return "redirect:/admin/main";
			}else {
				friendService.setUserStatus(user.getUsername(), "1");
				return "redirect:/user/main";
			}
		}
		else {
			model.addAttribute("msg", "帳號或密碼錯誤");
			return "login";
		}
	}
	
	// 登出
	@RequestMapping("/logout")
	public String logout(HttpServletResponse response, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		friendService.setUserStatus(user.getUsername(), "0");
		
		// 刪除cookie
		Cookie cookie = new Cookie("rememberMe", null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
		// 刪除session
		session.invalidate();
		
		return "redirect:/index";
	}
}
