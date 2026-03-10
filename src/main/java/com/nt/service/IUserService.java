package com.nt.service;

import java.util.Optional;

import com.nt.DTO.EmailDto;
import com.nt.DTO.ProfileRequest;
import com.nt.model.Users;

public interface IUserService
{
	public Boolean createProfile(String username,ProfileRequest profileRequest);
	public Optional<Users> findUser(String username);
	public String sendResetLink(EmailDto email);
	
	

	
}
