package com.nt.repositary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nt.model.Users;

public interface IUserRepo extends JpaRepository<Users, Long> {
	  Optional<Users> findByUsername(String username);
	  
	  
	  @Query("SELECT COUNT(DISTINCT u) FROM Users u JOIN u.authority a WHERE a.authority = :role")
	  Long countUsersByRole(@Param("role") String role);
	  
	 

		    // Count enabled users with a specific role
		    @Query("SELECT COUNT(u) FROM Users u JOIN u.authority a " +
		           "WHERE u.enabled = true AND a.authority = :role")
		    Long countByEnabledTrueAndRole(@Param("role") String role);

		
	  
	  Long countByEnabledFalse();


	

}
