package com.nt.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.DTO.MeetingDto;
import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Profile;
import com.nt.model.ScheduledMeeting;
import com.nt.model.SwapRequest;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IScheduleRepo;
import com.nt.repositary.ISwapRequest;

import jakarta.transaction.Transactional;

@Service("meetingService")
@Transactional
public class ScheduleMeetingImpl implements IScheduleMeetingSevice
{
	@Autowired
	private IScheduleRepo meetingRepo;
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
	private ISwapRequest swapRepo;
	@Autowired
	private IProfileService profileService;
	@Autowired
    private  ActivityLoggerService activityLoggerService;

	@Override
	public Boolean saveMeeting(MeetingDto dto)
	{
		Optional<SwapRequest> opt = swapRepo.findById(dto.getRequestId());
     	if(opt.isPresent())
		{
			SwapRequest swapRequest = opt.get();
			ScheduledMeeting meeting = new ScheduledMeeting();
			meeting.setTopicName(dto.getTopicName());
			meeting.setDurationMinutes(dto.getDurationMinutes());
			meeting.setMeetingDateTime(LocalDateTime.parse(dto.getMeetingDateTime()));
			meeting.setMeetingLink(dto.getMeetingLink());
			meeting.setOriginalRequest(swapRequest);
			meeting.setSender(swapRequest.getSender());
			meeting.setReceiver(swapRequest.getReceiver());
			
			Profile profile = profileRepo.findByUser(swapRequest.getReceiver().getUser()).orElseThrow();
			profile.setRequest(profile.getRequest()-1);
			profile.getMeetingsAsReceiver().add(meeting);
			profile.getMeetingsAsSender().add(meeting);
			swapRequest.setStatus("ACCEPT");
			meetingRepo.save(meeting);
			profileRepo.save(profile);
			swapRepo.save(swapRequest); 
			activityLoggerService.logActivity("CREATED", "@"+profile.getUser().getUsername()+" creates meeting!");
			return true;

		}
     	
     	return false;
	}
	
	@Override
	public List<ScheduledMeeting> getLearnerMeetingList(String username)
	{
		Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
		List<SwapRequest> swaps = profile.getSentRequest();
		List<Long> ids = new ArrayList<>();
		swaps.forEach(li -> {
			if(li.getStatus().equals("ACCEPT"))
			{
				ids.add(li.getId());
			}
			
		});
		
		List<ScheduledMeeting> learningMeetingList = meetingRepo.findAllById(ids);
		
		learningMeetingList.sort(Comparator.comparing(ScheduledMeeting::getMeetingDateTime).reversed());
		
		return learningMeetingList;
		
	}  
	
	@Override
	public List<ScheduledMeeting> getTearnerMeetingList(String username) 
	{
		Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
		List<SwapRequest> swaps = profile.getReceivedReqeust();
		List<Long> ids = new ArrayList<>();
		swaps.forEach(li -> {
			if(li.getStatus().equals("ACCEPT"))
			{
				ids.add(li.getId());
			}
			
		});
		
		List<ScheduledMeeting> teachingMeetingList = meetingRepo.findAllById(ids);
		
		teachingMeetingList.sort(Comparator.comparing(ScheduledMeeting::getMeetingDateTime).reversed());
		
		return teachingMeetingList;
	}
	
	@Override
	public void updateMeetingStatus(Long id) {
		ScheduledMeeting meeting = meetingRepo.findById(id).orElseThrow();
		SwapRequest req = meeting.getOriginalRequest();
		if(req.getStatus().equals("ACCEPT"))
		{
			req.setStatus("EXPIRED");
			swapRepo.save(req);		
			activityLoggerService.logActivity("REQUEST", meeting.getTopicName()+"topic meeting has expored");
		}
	
	}
	
	
	@Override
	public Boolean joinMeeting(Long id, String username) {
		ScheduledMeeting meeting = meetingRepo.findById(id).orElseThrow();
		Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
		SwapRequest swap = meeting.getOriginalRequest();
		if(meeting.getReceiver() == profile)
		{
			meeting.setTeacherJoined(true);
		}
		else if(meeting.getSender() == profile)
		{
			meeting.setLearnerJoined(true);
		}
		
		meetingRepo.save(meeting);
		LocalDateTime now = LocalDateTime.now();
		
		if(meeting.getLearnerJoined() && meeting.getTeacherJoined())
		{
			swap.setStatus("COMPLETED");
			swapRepo.save(swap);
			
			profileService.coinTransaction(meeting);
			activityLoggerService.logActivity("MEETING", "@"+meeting.getReceiver().getUser().getUsername()+" and @"+meeting.getSender().getUser().getUsername()+" joinde meeting!");
			return true;
		}
		
		
		return false;
	}
	
	@Override
	public List<ScheduledMeeting> historyList(String username) {
		Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
		//List<ScheduledMeeting> list = meetingRepo.findAllMeetingsForProfile(username);
		List<ScheduledMeeting> list = new ArrayList();
		meetingRepo.findAllMeetingsForProfile(username).forEach(li -> {
			if(!li.getOriginalRequest().getStatus().equals("ACCEPT"));
			{
				list.add(li);
			}
			
		});
		
		return list;
	}
	

}
