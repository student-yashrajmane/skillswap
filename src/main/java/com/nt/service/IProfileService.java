package com.nt.service;

import java.util.List;

import com.nt.DTO.ProfileRequest;
import com.nt.model.Profile;
import com.nt.model.ScheduledMeeting;

public interface IProfileService {
	public String getName(String name);
	public Profile getProfileData(String username);
	
	Profile findByGoogleId(String googleId);
	
	public Profile editProfile(String username);
	public Boolean updateProfile(ProfileRequest req,String username);
	public List<Profile> getAllProfiles();
	public List<Profile> getSearchedProfiles(String keyword);
	public void coinTransaction(ScheduledMeeting meeting);
}
