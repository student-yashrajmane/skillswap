package com.nt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto 
{
	private Long requestId;
	private String topicName;
	private Integer durationMinutes;
	private String meetingDateTime;
	private String meetingLink;
}
