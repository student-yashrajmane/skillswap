package com.nt.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingHistoryDto 
{
	private Long id;
	private String topicName;
	private Integer duration;
	private LocalDateTime meetingTimeAndDate;
	private String ReceiverName;
	private String senderName;
	private String status;
}
