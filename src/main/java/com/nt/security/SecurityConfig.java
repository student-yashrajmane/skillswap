package com.nt.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig  
{

    @Autowired private JwtFilter jwtFilter;
    
    @Autowired
    private CustomerUserDetailService userDetails;
    
    @Autowired
    private PasswordEncoder encode;
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
           .csrf(csrf -> csrf.disable()) // Disable CSRF for JWT
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS for React
            .authorizeHttpRequests(auth -> auth
            		
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/ws-activity/**").permitAll()
            
          //  .requestMatchers("/admin").permitAll()
            .requestMatchers("/api/user/**").hasRole("USER")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
          
            
                
                // 2. CATCH-ALL path last
         
             .anyRequest().authenticated() 
                
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://skillswap-frontend-hcl8.vercel.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // No need for allowCredentials(true) with JWT unless you use HttpOnly cookies
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        config.setAllowCredentials(true);
        return source;
    }
    
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(encode);
        return provider;
    }

 

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    
}