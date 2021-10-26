package com.syun.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.syun.pojo.User;
import com.syun.service.UserServiceImpl;

@Controller
public class AdminController {
	
	@Autowired
	private UserServiceImpl userService;
	
	@GetMapping("/admin/userList")
	public String userList(Model model) {
		List<User> users = userService.queryUserList();
		
		model.addAttribute("users", users);
		
		return "admin/userList";
	}
	
	@RequestMapping("delete/user/{id}")
	public String deleteUser(@PathVariable("id") int id) {
		userService.deleteUser(id);
		
		return "redirect:/admin/userList";
	}
}
