package com.nt.DTO;

import java.time.LocalDateTime;

import com.nt.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDetailsDTO 
{
	private Long id;
	private Profile profile;
	private String username;
	private Long earnedCoins;
	private Long lastQuizScore;
	private LocalDateTime lastAttempted;
	private LocalDateTime nextAttemp;
}
