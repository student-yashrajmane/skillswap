package com.nt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Profile;
import com.nt.model.SwapRequest;
import com.nt.model.Users;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.ISwapRequest;
import com.nt.repositary.IUserRepo;

@Service("skillswapService")
@Transactional
public class SkillSwapImpl implements ISkillSwapService
{
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
	private ISwapRequest swapRepo;
	@Autowired
    private  ActivityLoggerService activityLoggerService;

	@Override
	@Transactional
	public Boolean sendRequest(String senderName, String receiverName)
	{
		
		SwapRequest swapRequest = new SwapRequest();
		Optional<Users> sender = userRepo.findByUsername(senderName);
		Optional<Users> receiver = userRepo.findByUsername(receiverName);
		swapRequest.setSender(sender.get().getProfile());
		swapRequest.setReceiver(receiver.get().getProfile());
		swapRequest.setStatus("PENDING");
		if(receiver.isPresent())
		{
			if(sender.isPresent())
			{
				Profile profile = sender.get().getProfile();
				if(profile.getCoins()>=5)
				{
					Profile received = receiver.get().getProfile();
					received.setRequest(received.getRequest()+1);
					profileRepo.save(received);
					swapRepo.save(swapRequest);
					activityLoggerService.logActivity("REQUEST", "@"+sender.get().getUsername()+" send Request to @"+receiver.get().getUsername());
					return true;
					
					
				}
			}
		}
		return false;
	}
	  
	@Override
	public List<SwapRequest> getRequest(String username) {
		List<SwapRequest> list = swapRepo.getPendingRequests(username, "PENDING");
		return list;
	}
	
	
	@Override
	public String updateRequestStatus(Long id, String status) {
		SwapRequest req = swapRepo.findById(id).orElseThrow();
		req.setStatus(status);
		
		Profile profile = profileRepo.getProfileByUsername(req.getReceiver().getUser().getUsername());
		profile.setRequest(profile.getRequest()-1);
		profileRepo.save(profile);
		swapRepo.save(req);
		
		 activityLoggerService.logActivity("REQUEST", "@"+profile.getUser().getUsername()+" "+status+" request!");
		
		return "Request Rejected";
	}

}
