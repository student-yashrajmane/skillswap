package com.nt.adminService;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.nt.model.ActivitLogMessage;
import com.nt.repositary.IActivityLogRepo;

@Service("activityLoggerService")
public class ActivityLoggerService
{
	@Autowired
	private IActivityLogRepo activityLogRepo;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public void logActivity(String type,String message)
	{
		ActivitLogMessage logMsg = new ActivitLogMessage();
		logMsg.setType(type);
		logMsg.setMessage(message);
		logMsg.setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
		activityLogRepo.save(logMsg);
		
		Map<String,String> payload = new HashMap<>();
		payload.put("type", type);
		payload.put("message", message);
		payload.put("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
		messagingTemplate.convertAndSend("/topic/logs",payload);
	}
}
