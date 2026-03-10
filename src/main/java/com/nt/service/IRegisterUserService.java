package com.nt.service;

import java.util.Optional;

import com.nt.DTO.LoginRequest;
import com.nt.DTO.UserDTO;
import com.nt.model.Users;

public interface IRegisterUserService 
{
	public Boolean registerUser(UserDTO userDto);
	public Optional<Users> findForSignUp(LoginRequest log);
	public String resetPassword(String username,String password);
}
