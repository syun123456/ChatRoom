package com.syun.service;

import java.util.List;

import com.syun.pojo.User;

public interface UserService {
	List<User> queryUserList();
	
	List<String> searchUser(String username);
	
	User queryUserById(int id);
	
	User queryUserByUsername(String username);
	
	boolean usernameIsExist(String username);
	
	boolean passwordIsCorrect(String username, String password);
	
	int addUser(User user);
	
	int updateUser(User user);
	
	int deleteUser(int id);
}
