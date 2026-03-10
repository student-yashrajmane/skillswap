package com.nt.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service("quizSessionService")
public class QuizSessionService
{
	private final Map<String,List<Map<String,Object>>> quizStore = new ConcurrentHashMap<>();
	
	public void storeQuiz(String username,List<Map<String,Object>> quiz)
	{
		quizStore.put(username, quiz);
	}
	
	public List<Map<String,Object>> getQuiz(String username)
	{
		return quizStore.get(username);
	}
	
	public void removeQuiz(String username)
	{
		quizStore.remove(username);
	}
}
