package com.nt.service;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nt.DTO.LoginRequest;
import com.nt.DTO.UserDTO;
import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Authority;
import com.nt.model.Users;
import com.nt.repositary.IUserRepo;

@Service("registerUserService")
public class UserRegistrationImpl implements IRegisterUserService
{

    private final ActivityLoggerService activityLoggerService;
	@Autowired
	private IUserRepo userRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	


    UserRegistrationImpl(ActivityLoggerService activityLoggerService) {
        this.activityLoggerService = activityLoggerService;
    }

    
	@Override
	public Boolean registerUser(UserDTO userDto) {
		Optional<Users> opt = userRepo.findByUsername(userDto.getUsername());
		if(opt.isPresent())
		{
			return false;
		}
		else
		{
			Users user = new Users();
			user.setUsername(userDto.getUsername());
			user.setPassword(encoder.encode(userDto.getPassword()));
			user.setEnabled(true);
			
			Authority auth = new Authority();
			auth.setAuthority("ROLE_USER");
			auth.setUser(user);
			
			user.setAuthority(Set.of(auth));
			
			userRepo.save(user);
			activityLoggerService.logActivity("REGISTER", "New user @"+user.getUsername()+" just joined!");
			return true;
		}
	}

	@Override
	public Optional<Users> findForSignUp(LoginRequest log) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	@Override
	public String resetPassword(String username, String password) {
		Optional<Users> user = userRepo.findByUsername(username);
		if(user.isEmpty())
		{
			throw new RuntimeException("User not found");
		}
		Users data = user.get();
		data.setPassword(encoder.encode(password));
		userRepo.save(data);
		activityLoggerService.logActivity("RESET PASSWORD", "@"+data.getUsername()+" reset password!");
		return "User Password Updated";
	}

	

}
