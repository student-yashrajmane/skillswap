package com.nt.repositary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nt.model.Profile;
import com.nt.model.ScheduledMeeting;
import com.nt.model.Users;

public interface IProfileRepo extends JpaRepository<Profile, Long> {
	
	 @Query("SELECT p.fullName FROM Profile p WHERE p.user.username = :username")
	    String getFullNameByUsername(@Param("username") String username);
	 
	 @Query("SELECT p FROM Profile p WHERE p.user.username = :username")
	public Profile getProfileByUsername(@Param("username") String username);
	 
	// This allows the Service to check if the person has logged in before
	    Optional<Profile> findByGoogleId(String googleId);

		Optional<Profile> findByUser(Users users);
		
	
		Optional<Profile> findByUser_Username(String username);
		
		
		@Query("""
				   SELECT p FROM Profile p 
				   WHERE LOWER(p.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))
				      OR LOWER(p.professionalTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
				       OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
				""")
				List<Profile> searchProfiles(@Param("keyword") String keyword);
		
		
		
	

}
