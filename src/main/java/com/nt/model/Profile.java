package com.nt.model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String professionalTitle;
    
    @Column(columnDefinition = "TEXT")
    private String bio;

    private String skills;
    private Long coins = 5L;
    
    @Column(unique = true)
    private String googleId; 
    
    private Long request = 0L;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    // This allows the Controller to see the username but stops the recursion loop
    @JsonIgnoreProperties({"profile", "password", "authority", "enabled"})
    private Users user;
    
    @OneToMany(mappedBy = "sender")
    @JsonIgnore // Stops loop with SwapRequest
    private List<SwapRequest> sentRequest;
    
    @OneToMany(mappedBy = "receiver")
    @JsonIgnore // Stops loop with SwapRequest
    private List<SwapRequest> receivedReqeust;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore // Already ignored
    private List<ScheduledMeeting> meetingsAsSender;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonIgnore // Already ignored
    private List<ScheduledMeeting> meetingsAsReceiver;
    
    private LocalDateTime lastQuizAttemp;
    
    @OneToMany(mappedBy = "profile")
    @JsonIgnore // Stops loop with QuizDetails
    private List<QuizDetails> quizDetails;
}