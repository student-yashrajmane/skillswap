package com.nt.service;

import java.util.List;

import com.nt.model.SwapRequest;

public interface ISkillSwapService
{
	public Boolean sendRequest(String senderName,String receiverName);
	public List<SwapRequest> getRequest(String username);
	public String updateRequestStatus(Long id,String status);
}
