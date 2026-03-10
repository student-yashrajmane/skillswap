package com.nt.adminService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.model.Users;
import com.nt.repositary.IScheduleRepo;
import com.nt.repositary.IUserRepo;

@Service("frontPageService")
public class FrontPageServiceImpl implements IFrontDataService
{
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private IScheduleRepo meetingRepo;

	@Override
	public Map<String, Object> getAboutPage()
	{
		Long userCount = userRepo.countUsersByRole("ROLE_USER");
		Long swapedCount = meetingRepo.countMeetingsBySwapStatus("COMPLETED");
		
		Map<String,Object> map = new HashMap<>();
		map.put("userCount", userCount);
		map.put("swapedCount",swapedCount);
		return map;
	}

}
