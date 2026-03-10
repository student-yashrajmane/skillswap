package com.nt.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
    	System.out.println("SERVLET PATH = " + request.getServletPath());

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        
        if (request.getServletPath().equals("/api/auth/signin")
        		 || request.getServletPath().equals("/api/auth/signup")) {
        		    filterChain.doFilter(request, response);
        		    return;
        		}

        // 1. FIX: Changed || to && to prevent NullPointerException
        // Also added a check to ensure the header is long enough for substring(7)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                // If token is malformed or expired, we just log it. 
                // The request will continue but remain unauthenticated.
                logger.error("Could not extract username from token: " + e.getMessage());
            }
        }

        // 2. Validate and set Authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtUtils.isTokenValid(token)) {
                String role = jwtUtils.extractRole(token);
                
//                if(!role.startsWith("ROLE_"))
//                {
//                	role = "ROLE_"+role;
//                }
                
                // Ensure the role is not null before creating authorities
                if (role != null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, 
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Debug log
                    System.out.println("JWT Validated. User: " + username + " | Role: " + role);
                    
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println(
                    		   "SPRING AUTHORITIES = " +
                    		   SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    		);

                }
            }
        }
        System.out.println("SERVLET PATH = " + request.getServletPath());

        
        // 3. Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}