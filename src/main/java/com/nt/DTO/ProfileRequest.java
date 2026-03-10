package com.nt.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter 
@Setter
@NoArgsConstructor
@ToString
public class ProfileRequest {
	private String username;
    private String fullName;
    private String professionalTitle;
    private String bio;
    private String skills; // Raw string: "Java, React, SQL"
    private String googleId;
    private Long coins;
}