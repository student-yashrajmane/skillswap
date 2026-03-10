package com.nt.adminService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nt.DTO.ProfileRequest;
import com.nt.DTO.UserDTO;
import com.nt.model.Profile;
import com.nt.model.Users;
import com.nt.repositary.IAuthorityRepo;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.ISwapRequest;
import com.nt.repositary.IUserRepo;

@Service("/overviewDataService")
public class OverviewDataServiceImpl implements IOverviewDataService
{
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private ISwapRequest swapRepo;
	@Autowired
	private IAuthorityRepo authRepo;
	@Autowired
	private IProfileRepo profileRepo;

	@Override
	public Map<String, Object> getOverviewData() 
	{
		Long totalUsers = userRepo.countUsersByRole("ROLE_USER");
		Long activeUser = userRepo.countByEnabledTrueAndRole("ROLE_USER");
		Long blockedUser = userRepo.countByEnabledFalse();
		

		
		Map<String, Object> data = new HashMap<>();

		// Users
		data.put("totalUsers", totalUsers);
		data.put("activeUsers", activeUser);
		data.put("blockedUsers", blockedUser);


		
		return data;
	}
	
	@Override
	public List<UserDTO> getAllUsers() {
		
		
		List<UserDTO> list = new ArrayList();
		
		authRepo.findUsersByAuthority("ROLE_USER").forEach(user -> {
			UserDTO dto = new UserDTO();
			dto.setUsername(user.getUsername());
			dto.setIsEnable(user.getEnabled());
			list.add(dto);
		});
		
		return list;
	}
	
	@Override
	public ProfileRequest getProfile(String username){
		Optional<Profile> opt = profileRepo.findByUser_Username(username);
		if(opt.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile Not Found");
		}
		
		Profile profile = opt.get();
		ProfileRequest dto = new ProfileRequest();
		dto.setFullName(profile.getFullName());
		dto.setProfessionalTitle(profile.getProfessionalTitle());
		dto.setBio(profile.getBio());
		dto.setSkills(profile.getSkills());
		dto.setUsername(profile.getUser().getUsername());
		dto.setCoins(profile.getCoins());
		
		return dto;
		
	}
	
	@Override
	public void updateProfile(String username,ProfileRequest dto) {
		Optional<Profile> opt = profileRepo.findByUser_Username(username);
		if(opt.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile Not Found");
		}
		
		Profile profile = opt.get();
		profile.setFullName(dto.getFullName());
		profile.setProfessionalTitle(dto.getProfessionalTitle());
		profile.setBio(dto.getBio());
		profile.setSkills(dto.getSkills());
		profile.setCoins(dto.getCoins());
		profileRepo.save(profile);
		
		
	}
	
	@Override
	public Boolean toogleBlock(String username) {
		Optional<Users> opt = userRepo.findByUsername(username);
		if(opt.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}
		
		Users user = opt.get();
		if(user.getEnabled() == true)
		{
			user.setEnabled(false);
			userRepo.save(user);
			return false;
		}
		else
		{
			user.setEnabled(true);
			userRepo.save(user);
			return true;
		}
		
	}
	
	@Override
	public void deleteUser(String username) {
		Optional<Users> opt = userRepo.findByUsername(username);
		if(opt.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}
		
		Users user = opt.get();
		userRepo.delete(user);
		
	}

}
