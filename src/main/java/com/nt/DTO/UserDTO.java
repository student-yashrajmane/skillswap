package com.nt.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@NoArgsConstructor
@ToString
public class UserDTO 
{
	private String username;
	private String password;
	private Boolean isEnable;
}
