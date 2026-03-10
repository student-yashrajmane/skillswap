package com.nt.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
   
    private String username; // Or Email

    @Column(nullable = false)
   @JsonIgnore
    private String password; // Always stored as a hash (BCrypt)
    
  
  
  
    private Boolean enabled;
    
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Authority> authority;
    

    // Link to the profile
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER
    		, cascade = CascadeType.ALL)
  @JsonIgnore
    private Profile profile;
    
    
    
}