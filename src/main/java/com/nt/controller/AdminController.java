package com.nt.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.DTO.MeetingHistoryDto;
import com.nt.DTO.ProfileRequest;
import com.nt.DTO.QuizDetailsDTO;
import com.nt.DTO.SwapRequestDto;
import com.nt.DTO.UserDTO;
import com.nt.adminService.ActivityLoggerService;
import com.nt.adminService.IMeetingMgntService;
import com.nt.adminService.IOverviewDataService;
import com.nt.adminService.IQuizMgntService;
import com.nt.adminService.IRequestMgntService;
import com.nt.model.ActivitLogMessage;
import com.nt.repositary.IActivityLogRepo;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController
{
	@Autowired
	private IOverviewDataService overviewDataService;
	@Autowired
	private IMeetingMgntService meetingMgntService;
	@Autowired
	private IRequestMgntService requestMgntService;
	@Autowired
	private IQuizMgntService quizMgntService;
	@Autowired
	private IActivityLogRepo activityLogRepo;
	@Autowired
	private ActivityLoggerService activityLoggerService;
	
	
	@GetMapping("/overview-data")
	public ResponseEntity<?> getOverviewData()
	{
		try
		{
			Map<String,Object> data = overviewDataService.getOverviewData();
			return ResponseEntity.ok(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/all-users")
	public ResponseEntity<?> getAllUsers()
	{
		try
		{
			List<UserDTO> list = overviewDataService.getAllUsers();
			return ResponseEntity.ok(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/view-profile/{username}")
	public ResponseEntity<?> viewProfile(@PathVariable("username") String username)
	{
		ProfileRequest dto = overviewDataService.getProfile(username);
		return ResponseEntity.ok(dto);
	}
	
	@PutMapping("/update-profile/{username}")
	public ResponseEntity<?> updateProfile(@PathVariable("username") String username,@RequestBody ProfileRequest dto)
	{
		overviewDataService.updateProfile(username, dto);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("toggle-block/{username}")
	public ResponseEntity<?> toggleBlock(@PathVariable("username") String username)
	{
		Boolean toggle =overviewDataService.toogleBlock(username);
		return ResponseEntity.ok().build();
		
	}
	
	@DeleteMapping("/delete/{username}")
	public ResponseEntity<?> deleteUser(@PathVariable("username")String username)
	{
		overviewDataService.deleteUser(username);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/all-meetings")
	public ResponseEntity<?> getAllMeetings()
	{
		List<MeetingHistoryDto> list = meetingMgntService.getllMeetings();
		return ResponseEntity.ok(list);
	}
	
	@PutMapping("/cancel-meeting/{meetingId}")
	public ResponseEntity<?> cancelMeeting(@PathVariable("meetingId")Long id)
	{
		meetingMgntService.cancelMeeting(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/all-requests")
	public ResponseEntity<?> getAllRequests()
	{
		List<SwapRequestDto> list = requestMgntService.getAllRequest();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/all-quizData")
	public ResponseEntity<?> getAllQuizData()
	{
		List<QuizDetailsDTO> list = quizMgntService.getAllQuizDetails();
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/activities")
    public List<ActivitLogMessage> getHistory() {
        // We reverse it so React receives them in chronological order
        List<ActivitLogMessage> history = activityLogRepo.findAllByOrderByIdDesc();
        Collections.reverse(history);
        return history;
    }
	
	@GetMapping("/admin-logout")
	public ResponseEntity<?> adminLogout()
	{
		activityLoggerService.logActivity("LOGGED OUT", "@admin logged out!");
		activityLogRepo.deleteAll();
		
		return ResponseEntity.ok("Logged Out");
	}
	
	
}
