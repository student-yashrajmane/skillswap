package com.nt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nt.DTO.LearnerMeetingDto;
import com.nt.DTO.MeetingDto;
import com.nt.DTO.MeetingHistoryDto;
import com.nt.DTO.ProfileRequest;
import com.nt.DTO.SwapRequestDto;
import com.nt.DTO.TeachingMeetingDto;
import com.nt.DTO.UpdateRequestStatus;
import com.nt.adminService.ActivityLoggerService;
import com.nt.model.Profile;
import com.nt.service.IProfileService;
import com.nt.service.IScheduleMeetingSevice;
import com.nt.service.ISkillSwapService;
import com.nt.service.IUserService;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController 
{
	private final PasswordEncoder passwordEncoder;
	@Autowired
	private IProfileService profileService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISkillSwapService skillswapService;
	@Autowired
	private IScheduleMeetingSevice meetingService;
	@Autowired
	private ActivityLoggerService activityLoggerService;
	
	


    UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
	
	
	@PostMapping("/createProfile")
	public ResponseEntity<?> createProfileData(@RequestParam("username") String username,@RequestBody ProfileRequest profileRequest)
	{
		try {
		Boolean flag = userService.createProfile(username,profileRequest);
		
		if(flag)
		{
		//	String fullname = profileService.getName(profilereq.getFullName());
			System.out.println(profileRequest.getFullName());
			Map<String, String> response = new HashMap<>();
	        response.put("fullname", profileRequest.getFullName());
	    	System.out.println(profileRequest.getFullName());
	        return ResponseEntity.ok(response);
	       
		}
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
			        .body("User not found");
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
			        .body("Error");
		}
	
	}
	
	
	
	@GetMapping("/myprofile")
	public ResponseEntity<?> getMyProfile(@RequestParam("username") String username) {
	    Profile profile = profileService.getProfileData(username);
	    ProfileRequest dto = new ProfileRequest();
	    BeanUtils.copyProperties(profile, dto);
	    
	    if (profile == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
	    }
	    
	    // Return the object directly
	    return ResponseEntity.ok(dto); 
	}
	
	@GetMapping("/editProfile")
	public ResponseEntity<?> editProfile(@RequestParam("username") String username)
	{
		try {
		Profile profile = profileService.editProfile(username);
		
		Map<String,String> map = new HashMap<>();
		map.put("fullName",profile.getFullName());
		map.put("professionalTitle", profile.getProfessionalTitle());
		map.put("bio", profile.getBio());
		map.put("skills", profile.getSkills());
		
		return ResponseEntity.ok(map);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Something went wrong");
		}
	}
	
	
	@PutMapping("/updateProfile")
	public ResponseEntity<?> updateProfile(@RequestBody ProfileRequest req,@RequestParam("username")String username)
	{
		boolean flag = profileService.updateProfile(req, username);
		if(flag)
		{
			return ResponseEntity.status(HttpStatus.OK).body("Profile details updated");
		}
		else
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong");
		}
	}
	
	@GetMapping("/allProfiles")
	public ResponseEntity<?> getProfile(@RequestParam("username")String username)
	{
		List<ProfileRequest> list = new ArrayList<>();
		profileService.getAllProfiles().forEach(li -> {
			if(!li.getUser().getUsername().equals(username) && li.getUser().getEnabled() == true)
			{
				ProfileRequest dto = new ProfileRequest();
				BeanUtils.copyProperties(li, dto);
				dto.setUsername(li.getUser().getUsername());
				list.add(dto);		
			}
		});
		
		
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/user-dashboard")
	public ResponseEntity<?> userDashboadData(@RequestParam("username")String username)
	{
		Profile profile = profileService.getProfileData(username);
		Map<String,Object> map = new HashMap<>();
		map.put("coins",profile.getCoins());
		map.put("requests", profile.getRequest());
		return ResponseEntity.ok(map);
	}
	
	
	@GetMapping("/searched-profile")
	public ResponseEntity<?> getSearchProfile(@RequestParam("keyword")String keyword,@RequestParam("username")String username)
	{
		List<Profile> profiles = new ArrayList<>();
		profileService.getSearchedProfiles(keyword).forEach(li -> {
			
			if(!li.getUser().getUsername().equals(username))
			profiles.add(li);
		});
		return ResponseEntity.ok(profiles);
	}
	
	@GetMapping("/send-request")
	public ResponseEntity<?> sendRequest(@RequestParam("senderName")String senderName,@RequestParam("receiverName")String receiverName)
	{
		Boolean flag = skillswapService.sendRequest(senderName, receiverName);
		if(flag)
		{
			return ResponseEntity.status(HttpStatus.OK).body("Request Sent");
		}
		else
		{
			return ResponseEntity.status(HttpStatus.OK).body("Not enough coins! Earn more by completing today's quiz. (5 coins required)");
		}
		
	}
	
	@GetMapping("/received-request")
	public ResponseEntity<?> getRequest(@RequestParam("username")String username)
	{
		List<SwapRequestDto> list = new ArrayList<>();
		skillswapService.getRequest(username).forEach(li -> {
			SwapRequestDto dto = new SwapRequestDto();
			dto.setId(li.getId());
			dto.setReceiverName(li.getReceiver().getFullName());
			dto.setRequestDate(li.getRequestDate());
			dto.setSenderName(li.getSender().getFullName());
			list.add(dto);
			
			
			
		});
		return ResponseEntity.ok(list);
		
	}
	
	@PostMapping("/update-request-status")
	public ResponseEntity<?> handleRequest(@RequestParam("requestId")Long id,@RequestParam("status")String status)
	{
		String msg = skillswapService.updateRequestStatus(id, status);
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
	
	@PostMapping("/set-meeting")
	public ResponseEntity<?> setMeeting(@RequestBody MeetingDto meetingDto)
	{
		Boolean msg = meetingService.saveMeeting(meetingDto);
		if(msg)
		{
			return ResponseEntity.status(HttpStatus.OK).body("Meeting Created");
		}
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting bot created");
		}
	}
	
	@GetMapping("/meeting-learner-list")
	public ResponseEntity<?> learnerMeetingList(@RequestParam("username")String username)
	{
		List<LearnerMeetingDto> list = new ArrayList<>();
		 meetingService.getLearnerMeetingList(username).forEach(li -> {
			 LearnerMeetingDto dto = new LearnerMeetingDto();
			 dto.setId(li.getId());
			 dto.setTopicName(li.getTopicName());
			 dto.setDuration(li.getDurationMinutes());
			 dto.setMeetingDatetime(li.getMeetingDateTime());
			 dto.setMeetingLink(li.getMeetingLink());
			 dto.setSenderName(li.getReceiver().getFullName());
			 list.add(dto);
			 
		 });
		
		return ResponseEntity.ok(list);
	}
	
	
	@GetMapping("/meeting-teacher-list")
	public ResponseEntity<?> teachingMeetingList(@RequestParam("username")String username)
	{
		List<TeachingMeetingDto> list = new ArrayList<>();
		meetingService.getTearnerMeetingList(username).forEach(li -> {
			TeachingMeetingDto dto = new TeachingMeetingDto();
			dto.setId(li.getId());
			dto.setTopicName(li.getTopicName());
			dto.setDuration(li.getDurationMinutes());
			dto.setMeetingDatetime(li.getMeetingDateTime());
			dto.setMeetingLink(li.getMeetingLink());
			dto.setReceiverName(li.getSender().getFullName());
			list.add(dto);
		});
		
		return ResponseEntity.ok(list);
	}
	
	
	
	@PostMapping("/update-meeting-status")
	public ResponseEntity<?> updateRequestStatus(@RequestParam("id")Long id)
	{
		try {
			meetingService.updateMeetingStatus(id);
			return ResponseEntity.status(HttpStatus.OK).body("Session Expired");
		}catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
		}
		
		
	}
	
	@PostMapping("/join-meeting")
	public ResponseEntity<?> joinMeeting(@RequestParam("meetingId")Long id,@RequestParam("username")String username)
	{
		Boolean flag = meetingService.joinMeeting(id, username);
		if(flag)
		{
			return ResponseEntity.ok("Both joined meeting!");
		}
		else
		{
			return ResponseEntity.ok("Waiting for another to join meeting");
		}
		
	}
	
	@GetMapping("/meeting-history")
	public ResponseEntity<?> meetingHistory(@RequestParam("username")String username)
	{
		List<MeetingHistoryDto> list = new ArrayList<>();
		meetingService.historyList(username).forEach(li -> {
			MeetingHistoryDto dto = new MeetingHistoryDto();
			dto.setTopicName(li.getTopicName());
			dto.setDuration(li.getDurationMinutes());
			dto.setMeetingTimeAndDate(li.getMeetingDateTime());
			dto.setReceiverName(li.getReceiver().getFullName());
			dto.setSenderName(li.getSender().getFullName());
			dto.setStatus(li.getOriginalRequest().getStatus());
			System.out.println(dto.getMeetingTimeAndDate());
			list.add(dto);
		});
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/user-logout/{username}")
	public ResponseEntity<?> userLogout(@PathVariable("username")String username)
	{
		activityLoggerService.logActivity("LOGGED OUT", "@"+username+" logged out!");
		return ResponseEntity.ok("Logged Out");
	}
	
}



//List<SwapRequest> list = skillswapService.getRequest(username);
//for (SwapRequest request : list) {
//    System.out.println(
//        "ID: " + request.getId() +
//        ", Status: " + request.getStatus() +
//        ", Receiver: " + request.getReceiver().getUsername() +
//        ", Sender: " + request.getSender().getProfile().getFullName()
//    );
//});
