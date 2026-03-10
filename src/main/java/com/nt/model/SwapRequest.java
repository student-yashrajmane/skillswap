package com.nt.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SwapRequest 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_profile_id",nullable = false)
 // This ensures the sender's username is ALWAYS included in the JSON for all 4 items
 @JsonIgnoreProperties({"profile", "authority", "password", "enabled", "hibernateLazyInitializer", "handler"})
 private Profile sender;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "receiver_profile_id",nullable = false)
 // This prevents the receiver object from causing a recursive loop back to the request
 @JsonIgnoreProperties({"profile", "authority", "password", "enabled", "hibernateLazyInitializer", "handler"})
 private Profile receiver;

   
    private String status; // "PENDING", "ACCEPTED", "DECLINED"
    
    private LocalDateTime requestDate = LocalDateTime.now();

 
}

