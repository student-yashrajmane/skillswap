package com.nt.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service("geminiQuizService")
public class GeminiQuizImpl implements IGemineQuiz
{
	@Value("${gemini.api.key}")
	private String apiKey;
	
	private final String URL =
		    "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

	@Override
	public String generateQuiz(String skill) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		String prompt = """
				Generate 10 multiple choice questions on skill: %s.
				Difficulty: Medium.

				Return ONLY raw JSON array.
				Do NOT wrap in markdown.
				Do NOT include ```json.
				Do NOT add explanation.

				Format strictly like this:
				[
				  {
				    "question": "...",
				    "options": ["A","B","C","D"],
				    "correctAnswer": "....."
				  }
				]
				""".formatted(skill);
		
		Map<String,Object> body = Map.of(
				"contents",List.of(
						Map.of("parts",List.of(
								Map.of("text",prompt)
								))
						)
				);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String,Object>> request = new HttpEntity<>(body,headers);
		
		ResponseEntity<String> response = 
				restTemplate.postForEntity(URL + apiKey, request, String.class);
		
		return response.getBody();
	
	}

}
