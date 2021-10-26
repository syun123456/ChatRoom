package com.syun.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.syun.pojo.User;

@Mapper
@Repository
public interface UserMapper {
	List<User> queryUserList();
	
	List<String> searchUser(@Param("username")String username);
	
	User queryUserById(int id);
	
	User queryUserByUsername(@Param("username")String username);
	
	int addUser(User user);
	
	int updateUser(User user);
	
	int deleteUser(int id);
}
