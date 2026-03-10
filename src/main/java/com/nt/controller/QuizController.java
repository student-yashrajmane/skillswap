package com.nt.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.adminService.ActivityLoggerService;
import com.nt.adminService.IQuizMgntService;
import com.nt.model.Profile;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IUserRepo;
import com.nt.service.IGemineQuiz;
import com.nt.service.IQuizAttempService;
import com.nt.service.QuizSessionService;



@RestController
@RequestMapping("/api/quiz")
public class QuizController
{
	@Autowired
	private IGemineQuiz geminiQuizService;
	@Autowired
	private QuizSessionService quizSessionService;
	@Autowired
	private IUserRepo userRepo;	
	@Autowired
	private IProfileRepo profileRepo;
	@Autowired
	private IQuizAttempService quizAttemptService;
	@Autowired
	private IQuizMgntService quizMgntService;
	@Autowired
	private  ActivityLoggerService activityLoggerService;
	
	
	@PostMapping("/generate")
	public ResponseEntity<?> generate(@RequestParam("username")String username)
	{
		try {
		
		Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
	

		Boolean check = quizAttemptService.checkAvailiblity( profile);
		if(!check)
		{
			LocalDateTime nextAttempt = profile.getLastQuizAttemp().plusHours(24);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
			String formattedTime = nextAttempt.format(formatter);

			return ResponseEntity.status(HttpStatus.FORBIDDEN)
			        .body("Your daily quiz attempt has already been used. The next attempt will be available after " + formattedTime + ".");
		}
	
		String raw = geminiQuizService.generateQuiz(profile.getSkills());
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode root = mapper.readTree(raw);
		
		String text = root
				.get("candidates")
				.get(0)
				.get("content")
				.get("parts")
				.get(0)
				.get("text")
				.asText();
		
		List<Map<String,Object>> quiz = mapper.readValue(text, List.class);
		
		quizSessionService.storeQuiz(username, quiz);
		
		List<Map<String,Object>> quizForUser = quiz.stream()
			    .map(q -> {
			        Map<String,Object> copy = new HashMap<>(q);
			        copy.remove("correctAnswer");
			        return copy;
			    })
			    .toList();
		 
		 activityLoggerService.logActivity("QUiZ", "@"+username+" Started quiz!");

		
		return ResponseEntity.ok(quizForUser);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating quiz");
		}
			
	}
	
	
	@PostMapping("/submit")
	public ResponseEntity<?> submit(@RequestBody Map<String,String> answers,Principal principal)
	{
		String username = principal.getName();
		
		List<Map<String,Object>> quiz = quizSessionService.getQuiz(username);
		
		if(quiz == null)
		{
		
			return ResponseEntity.badRequest().body("Quiz Expired");
		}
		
		int score = 0;
		System.out.println(quiz.get(0).get("correctAnswer")+"-----------------------------------------------------");
		System.out.println(answers.keySet()+"-----------------------------------------------------");
		for (int i = 0; i < quiz.size(); i++) {
		 String correct = (String)quiz.get(i).get("correctAnswer");
		 String answer = answers.get(String.valueOf(i));
		 System.out.println(correct+"-----");
		 System.out.println(answer+"-------");
		 if(correct.equals(answer))
		 {
			 score++;
		 }
		}
        Profile profile = profileRepo.findByUser_Username(username).orElseThrow();
		
		profile.setCoins(profile.getCoins()+score);
		
		quizMgntService.saveQuizData(LocalDateTime.now(),score,username);
		
		profileRepo.save(profile);
		quizSessionService.removeQuiz(username);
		
		 activityLoggerService.logActivity("QUiZ", "@"+username+" Submited quiz and earned "+score+" coins!");
		return ResponseEntity.ok(Map.of("score",score,"earnedCoins",score));
	}
}
