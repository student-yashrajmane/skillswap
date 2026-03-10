package com.nt.adminService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.DTO.SwapRequestDto;
import com.nt.model.SwapRequest;
import com.nt.repositary.ISwapRequest;

@Service("requestMngtService")
public class RequestMgntServiceImpl implements IRequestMgntService
{
	
	@Autowired
	private ISwapRequest requestRepo;

	@Override
	public List<SwapRequestDto> getAllRequest() {
		
		List<SwapRequestDto> list = new ArrayList<>();
		
		requestRepo.findAll().forEach(li -> {
			SwapRequestDto dto = new SwapRequestDto();
			dto.setReceiverName(li.getReceiver().getUser().getUsername());
			dto.setSenderName(li.getSender().getUser().getUsername());
			dto.setRequestDate(li.getRequestDate());
			dto.setStatus(li.getStatus());
			list.add(dto);
			
		});
		
		return list;
	}

}
