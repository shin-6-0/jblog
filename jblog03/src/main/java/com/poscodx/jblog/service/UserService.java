package com.poscodx.jblog.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poscodx.jblog.vo.UserVo;
import com.poscodx.jblog.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	public void join(@Valid UserVo userVo) {
		System.out.println("Join UserVo >> "+userVo);
		userRepository.insert(userVo);
		
	}
	
	public UserVo getUser(String id, String password) {
		return userRepository.findByEmailAndPassword(id, password);
	}

}
