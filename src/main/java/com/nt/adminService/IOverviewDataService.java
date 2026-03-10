package com.nt.adminService;

import java.util.List;
import java.util.Map;

import com.nt.DTO.ProfileRequest;
import com.nt.DTO.UserDTO;

public interface IOverviewDataService 
{
	public Map<String,Object> getOverviewData();
	public List<UserDTO> getAllUsers();
	public ProfileRequest getProfile(String username);
	public void updateProfile(String username,ProfileRequest dto);
	public Boolean toogleBlock(String username);
	public void deleteUser(String username);
}
