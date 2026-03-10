package com.nt.adminService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nt.DTO.MeetingHistoryDto;
import com.nt.model.ScheduledMeeting;
import com.nt.repositary.IScheduleRepo;

@Service("meetingMgntService")
public class MeetingMgntServiceImpl implements IMeetingMgntService
{
	@Autowired
	private IScheduleRepo meetingRepo;

	@Override
	public List<MeetingHistoryDto> getllMeetings() {
		List<MeetingHistoryDto> list = new ArrayList();
		meetingRepo.findAll().forEach(li -> {
			MeetingHistoryDto dto = new MeetingHistoryDto();
			dto.setId(li.getId());
			dto.setDuration(li.getDurationMinutes());
			dto.setMeetingTimeAndDate(li.getMeetingDateTime());
			dto.setReceiverName(li.getReceiver().getUser().getUsername());
			dto.setSenderName(li.getSender().getUser().getUsername());
			dto.setTopicName(li.getTopicName());
			dto.setStatus(li.getOriginalRequest().getStatus());
			
			list.add(dto);
			
		});
		
		return list;
	}
	
	@Override
	public void cancelMeeting(Long id) {
		
		Optional<ScheduledMeeting> meeting = meetingRepo.findById(id);
		if(meeting.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting Not Found");
		}
		
		meeting.get().getOriginalRequest().setStatus("CANCELLED");
		meetingRepo.save(meeting.get());
		
	}

}
