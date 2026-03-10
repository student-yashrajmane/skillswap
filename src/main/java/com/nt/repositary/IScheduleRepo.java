package com.nt.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nt.model.ScheduledMeeting;

public interface IScheduleRepo extends JpaRepository<ScheduledMeeting, Long>
{
	@Query("""
		       SELECT m
		       FROM ScheduledMeeting m
		       WHERE m.sender.user.username = :username
		          OR m.receiver.user.username = :username
		       ORDER BY m.meetingDateTime DESC
		       """)
		List<ScheduledMeeting> findAllMeetingsForProfile(@Param("username") String username);
	
	
	@Query("""
		       SELECT COUNT(DISTINCT m)
		       FROM ScheduledMeeting m
		       JOIN m.originalRequest r
		       WHERE r.status = :status
		       """)
		Long countMeetingsBySwapStatus(@Param("status") String status);
}
