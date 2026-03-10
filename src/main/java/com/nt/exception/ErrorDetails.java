package com.nt.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails 
{
	private LocalDateTime timestamp;
	private String status;
	private String error;
	private String msg;
}
