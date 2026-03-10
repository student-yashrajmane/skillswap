package com.nt.adminService;

import java.util.List;

import com.nt.DTO.MeetingHistoryDto;

public interface IMeetingMgntService 
{
	public List<MeetingHistoryDto> getllMeetings();
	public void cancelMeeting(Long id);
}
