package com.nt.adminService;

import java.time.LocalDateTime;
import java.util.List;

import com.nt.DTO.QuizDetailsDTO;

public interface IQuizMgntService {
	
	public void saveQuizData(LocalDateTime time,Integer score,String username);
	public List<QuizDetailsDTO> getAllQuizDetails();

}
