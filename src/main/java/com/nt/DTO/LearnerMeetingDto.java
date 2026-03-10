package com.nt.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerMeetingDto 
{
	private Long id;
	private String topicName;
	private Integer duration;
	private LocalDateTime meetingDatetime;
	private String meetingLink;
	private String senderName;
}
