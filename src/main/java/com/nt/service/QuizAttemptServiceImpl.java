package com.nt.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Profile;
import com.nt.repositary.IProfileRepo;

@Service("quizAttemptService")
public class QuizAttemptServiceImpl implements IQuizAttempService
{
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
    private  ActivityLoggerService activityLoggerService;

	@Override
	public Boolean checkAvailiblity(Profile profile) {
		
	LocalDateTime now = LocalDateTime.now();
	LocalDateTime lastTry = profile.getLastQuizAttemp();
	if(lastTry!=null && lastTry.plusHours(24).isAfter(now))
	{
		return false;
	}
	else
	{
		profile.setLastQuizAttemp(now);
		profileRepo.save(profile);
	//	activityLoggerService.logActivity("QUIZ", "@"+profile.getUser().getUsername()+" attempted quiz!");
		return true;
	}
	}

}
