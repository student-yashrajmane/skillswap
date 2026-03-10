package com.nt.service;

import java.util.List;

import com.nt.DTO.MeetingDto;
import com.nt.model.ScheduledMeeting;

public interface IScheduleMeetingSevice
{
	public Boolean saveMeeting(MeetingDto dto);
	public List<ScheduledMeeting> getLearnerMeetingList(String username);
	public List<ScheduledMeeting> getTearnerMeetingList(String username);
	public void updateMeetingStatus(Long id);
	public Boolean joinMeeting(Long id,String username);
	public List<ScheduledMeeting> historyList(String username);
}
