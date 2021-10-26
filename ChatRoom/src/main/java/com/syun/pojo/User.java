package com.syun.pojo;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
	private int id;
	private String username;
	private String password;
	private String gender;
	private Date birth;
	private String phone;
	private String address;
	private String email;
	
	public User() {
		
	}
	public User(int id, String username, String password, String gender, Date birth, String phone, String address,
			String email) {
		super();
		this.id = id;
		this.username = username.trim();
		this.password = password.trim();
		this.gender = gender;
		this.birth = birth;
		this.phone = phone.trim();
		this.address = address.trim();
		this.email = email.trim();
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", gender=" + gender
				+ ", birth=" + birth + ", phone=" + phone + ", address=" + address + ", email=" + email + "]";
	}
}
