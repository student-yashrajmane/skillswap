package com.nt.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nt.DTO.EmailDto;
import com.nt.DTO.ProfileRequest;
import com.nt.adminService.ActivityLoggerService;
import com.nt.email.EmailService;
import com.nt.model.PasswordResetToken;
import com.nt.model.Profile;
import com.nt.model.Users;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IScheduleRepo;
import com.nt.repositary.IUserRepo;
import com.nt.repositary.PasswordResetTokenRepo;

@Service("userService")
public class UserServiceImpl implements IUserService
{

    private final ProfileServiceImpl profileService;
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
	private IScheduleRepo meetingRepo;
	@Autowired
	private EmailService emailService;
	@Autowired
	private PasswordResetTokenRepo tokenRepo;
	@Autowired
	private ActivityLoggerService activityLoggerService;
	


    UserServiceImpl(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }
	
	
	@Override
	public Boolean createProfile(String username,ProfileRequest profileRequest) {

	    Optional<Users> opt = userRepo.findByUsername(username);

	    if (opt.isPresent()) {

	        Users users = opt.get();

	        // 🔥 Check if profile already exists
	        Optional<Profile> existingProfile = profileRepo.findByUser(users);

	        if (existingProfile.isPresent()) {
	            return false;  // or throw exception
	        }

	        Profile profile = new Profile();
	        BeanUtils.copyProperties(profileRequest, profile);
	        profile.setCoins(5L);
	        profile.setUser(users);

	        activityLoggerService.logActivity("PROFILE", "@"+username+" created profile!");

	        
	        profileRepo.save(profile);
	      
	        

	        return true;
	    }

	    return false;
	}
	
	
	@Override
	public Optional<Users> findUser(String username) {
		return userRepo.findByUsername(username);	
	}
	
	@Override
	
	public String sendResetLink(EmailDto email) 
	{
		Optional<Users> user = userRepo.findByUsername(email.getUsername());
		if(user.isEmpty())
		{
			throw new RuntimeException( "User not found");
		}
		
		tokenRepo.findByUser(user.get())
        .ifPresent(tokenRepo::delete);
		
		String token = UUID.randomUUID().toString();
		
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(token);
		resetToken.setUser(user.get());
		resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
		resetToken.setUsed(false);
		
		tokenRepo.save(resetToken);
		
		emailService.sendResetEmail(email.getEmail(), token);
		 activityLoggerService.logActivity("PASSWORD", "Reset link to @"+user.get().getUsername());
		return "Reset password link has sent to your email";
		
	}
	
	

	
	

}
