package com.nt.adminService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.nt.DTO.QuizDetailsDTO;
import com.nt.model.Profile;
import com.nt.model.QuizDetails;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IQuizDetailsRepo;

@Service("quizMgntService")
class QuizMgntServiceImpl implements IQuizMgntService
{
	@Autowired
	private IQuizDetailsRepo quizDetailsRepo;
	@Autowired
	private IProfileRepo profileRepo;

	@Override
	@Transactional
	public void saveQuizData(LocalDateTime time, Integer score,String username) {
		
		Optional<Profile> opt = profileRepo.findByUser_Username(username);
		if(opt.isEmpty())
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile Not found");
		}
		
	
		QuizDetails quizDetails = new QuizDetails();
		quizDetails.setEarnedCoins((long)score);
		quizDetails.setLastAttempted(time);
		quizDetails.setLastQuizScore((long)score);
		quizDetails.setNextAttemp(time.plusHours(24));
		quizDetails.setProfile(opt.get());
		
		quizDetailsRepo.save(quizDetails);		
	}
	
	@Override
	public List<QuizDetailsDTO> getAllQuizDetails() {
		
		List<QuizDetailsDTO> list = new ArrayList<>();
		quizDetailsRepo.findAll().forEach(li -> {
			
			QuizDetailsDTO dto = new QuizDetailsDTO();
			dto.setUsername(li.getProfile().getUser().getUsername());
			dto.setEarnedCoins(li.getEarnedCoins());
			dto.setLastAttempted(li.getLastAttempted());
			dto.setNextAttemp(li.getNextAttemp());
			dto.setLastQuizScore(li.getLastQuizScore());
			list.add(dto);
		});	
		
		return list;
		
	}
	

	
}
