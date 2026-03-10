package com.nt.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.nt.model.Users;
import com.nt.repositary.IUserRepo;

@Component
public class CustomerUserDetailService implements UserDetailsService
{
	@Autowired
	private IUserRepo userRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    Users user = userRepo.findByUsername(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

	    return new org.springframework.security.core.userdetails.User(
	        user.getUsername(), 
	        user.getPassword(), 
	        user.getAuthority().stream()
	            .map(auth -> {
	                String roleName = auth.getAuthority();
	                // Ensure the prefix is added if not present in DB
	                if (!roleName.startsWith("ROLE_")) {
	                    roleName = "ROLE_" + roleName;
	                }
	                return new SimpleGrantedAuthority(roleName);
	            })
	            .collect(Collectors.toList())
	    );
	}

}
