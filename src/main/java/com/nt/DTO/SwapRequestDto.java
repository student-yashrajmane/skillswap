package com.nt.DTO;

import java.time.LocalDateTime;

import com.nt.model.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@NoArgsConstructor
@ToString
public class SwapRequestDto 
{
		private Long id;
		private String senderName;
		private String receiverName;
		private String status;
		private LocalDateTime requestDate;
}
