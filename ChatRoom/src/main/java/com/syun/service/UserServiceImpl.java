package com.syun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.syun.dao.UserMapper;
import com.syun.pojo.User;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public List<User> queryUserList() {
		return userMapper.queryUserList();
	}
	
	@Override
	public List<String> searchUser(String username){
		return userMapper.searchUser(username + "%");
	}
	
	@Override
	public User queryUserById(int id) { 
		return userMapper.queryUserById(id);
	}
	
	@Override
	public User queryUserByUsername(String username) {
		return userMapper.queryUserByUsername(username);
	}
	
	
	@Override
	public boolean usernameIsExist(String username) {
		if(userMapper.queryUserByUsername(username) == null) {
			return false;
		}else {
			return true;
		}
	}
	
	@Override
	public boolean passwordIsCorrect(String username, String password) {
		User user = userMapper.queryUserByUsername(username);
		return user.getPassword().equals(password);
	}
	
	@Override
	public int addUser(User user) {
		return userMapper.addUser(user);
	}

	@Override
	public int updateUser(User user) {
		return userMapper.updateUser(user);
	}

	@Override
	public int deleteUser(int id) {
		return userMapper.deleteUser(id);
	}

}
