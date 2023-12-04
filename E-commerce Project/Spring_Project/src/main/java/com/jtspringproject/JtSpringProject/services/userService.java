package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.userDao;
import com.jtspringproject.JtSpringProject.models.User;

@Service
public class userService {
	@Autowired
	private userDao userDao;
	
	public List<User> getUsers(){
		return this.userDao.getAllUser();
	}
	
	public void addUser(User user) {
		this.userDao.saveUser(user);
	}
	
	public User checkLogin(String username,String password) {
		return this.userDao.getUser(username, password);
	}


	public boolean deleteUser(int id) {
		return this.userDao.deleteUser(id);
	}

	public User updateUser(User user) {
		return this.userDao.updateUser(user);
	}

	public User findUserById(int id)
	{
		return userDao.getUserById(id);
	}
}
