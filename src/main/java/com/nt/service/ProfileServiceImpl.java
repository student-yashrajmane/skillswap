package com.nt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.DTO.ProfileRequest;
import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Profile;
import com.nt.model.ScheduledMeeting;
import com.nt.model.Users;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IScheduleRepo;
import com.nt.repositary.IUserRepo;

import jakarta.transaction.Transactional;

@Service("profileService")
public class ProfileServiceImpl implements IProfileService
{
	@Autowired
    private final ActivityLoggerService activityLoggerService;
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private IScheduleRepo meetingRepo;

    ProfileServiceImpl(ActivityLoggerService activityLoggerService) {
        this.activityLoggerService = activityLoggerService;
    }
	
	@Override
	public String getName(String name) {
		String fullname = profileRepo.getFullNameByUsername(name);
		return fullname;
	}
	
	@Override
	public Profile getProfileData(String username) {
		Profile profile = profileRepo.getProfileByUsername(username);
		return profile;
	}
	
	@Override
    public Profile findByGoogleId(String googleId) {
        // Return the profile if found, otherwise return null to trigger the "isNewUser" path
        return profileRepo.findByGoogleId(googleId).orElse(null);
    }
	
	
	@Override
	public Profile editProfile(String username) {
		
		Optional<Profile> opt = profileRepo.findByUser_Username(username);
		
		return opt.get();
		
	}
	
	@Override
	public Boolean updateProfile(ProfileRequest req, String username) {
		Optional<Users> opt = userRepo.findByUsername(username);
		if(opt.isPresent())
		{
			Profile profile = opt.get().getProfile();
			profile.setFullName(req.getFullName());
			profile.setProfessionalTitle(req.getProfessionalTitle());
			profile.setBio(req.getBio());
			profile.setSkills(req.getSkills());
			
			profileRepo.save(profile);
			activityLoggerService.logActivity("EDIT", "@"+profile.getUser().getUsername()+" edited profile!");
			return true;
		}
		else
		{
			return true;
		}
	}
	
	
	@Override
	public List<Profile> getAllProfiles() {
		return profileRepo.findAll();
	}
	
	@Override
	public List<Profile> getSearchedProfiles(String keyword) {
		return profileRepo.searchProfiles(keyword);
	}
	
	@Override
	@Transactional
	public void coinTransaction(ScheduledMeeting meeting) {
		
		ScheduledMeeting session = meeting;
		Profile learner = session.getSender();
		Profile teacher = session.getReceiver();
		
		learner.setCoins(learner.getCoins()-5);
		teacher.setCoins(teacher.getCoins()+5);
		
		profileRepo.save(learner);
		profileRepo.save(teacher);
		meetingRepo.save(session);		
		activityLoggerService.logActivity("SWAPPED", "@"+teacher.getUser().getUsername()+" received coins from "+learner.getUser().getUsername());
	}
}
