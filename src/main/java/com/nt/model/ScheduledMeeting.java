package com.nt.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topicName;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private LocalDateTime meetingDateTime;

    @Column(nullable = false)
    private String meetingLink;

    // The person who originally asked for the skill
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Profile sender;

    // The person who accepted and set up the meeting
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Profile receiver;

    // Optional: Reference to the original swap request
    @OneToOne
    private SwapRequest originalRequest;
    
    private Boolean learnerJoined = false;
    private Boolean teacherJoined = false;
}