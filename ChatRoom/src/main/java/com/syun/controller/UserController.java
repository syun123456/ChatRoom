package com.syun.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.syun.pojo.User;
import com.syun.service.UserServiceImpl;
import com.syun.utils.UploadFileUtils;

@Controller
public class UserController {
	
	@Autowired
	private UserServiceImpl userService;
	
	// 轉發到帳號建立頁面
	@GetMapping("/user/add")
	public String userAddPage() {
		return "user/add";
	}
	
	// 轉發到帳號修改頁面
	@GetMapping("/user/update")
	public String userUpdatePage(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		model.addAttribute("user", user);
		return "user/update";
	}
	
	// 帳號建立
	@PostMapping("/user/add")
	public String userAdd(User user, Model model) {
		// 判斷帳號是否已重複
		if(userService.queryUserByUsername(user.getUsername()) != null) {
			model.addAttribute("msg", "該帳號已存在，請重新輸入!");
			return "user/add";
		}
		
		userService.addUser(user);
		model.addAttribute("msg", "帳號註冊成功!");
		
		// 設置預設頭像
		UploadFileUtils uploadFile = new UploadFileUtils();
		uploadFile.setAvater(user.getUsername(), null);
		
		return "user/success";
	}
	
	// 帳號修改
	@PostMapping("/user/update")
	public String userSuccess(User user, Model model) {
		userService.updateUser(user);
		model.addAttribute("msg", "帳號修改成功!");
		return "user/success";
	}
	
	@PostMapping("/user/add/ajax")
	@ResponseBody
	public String userAjax(String username) {
		if(username == "") {
			return " 帳號不得為空，請重新輸入!";
		}
		
		if(username != null) {
			if(userService.queryUserByUsername(username) != null) {
				return " 該帳號已存在，請重新輸入!";
			}
		}
		
		return " 該帳號可以使用!";
	}
	
	// 轉發到設置頭貼頁面
	@GetMapping("/user/avatar")
	public String userAvatar() {
		return "user/avatar";
	}
	
	// 上傳頭貼
	@PostMapping("/user/upload")
	public String userUpload(MultipartFile  file, HttpSession session, Model model){
		User user = (User) session.getAttribute("loginUser");
		
		UploadFileUtils uploadFile = new UploadFileUtils();
		
		String msg = uploadFile.uploadAvater(user.getUsername(), file);
		
		model.addAttribute("msg", msg);
		
		if(!msg.contains("成功")) {
			return "user/avatar";
		}
		
		return "user/success";
	}
}
