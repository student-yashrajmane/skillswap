package com.nt.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nt.model.SwapRequest;

public interface ISwapRequest extends JpaRepository<SwapRequest, Long> {
	
	@Query("""
		    SELECT r FROM SwapRequest r
		    JOIN FETCH r.sender s
		    JOIN FETCH s.user
		    JOIN FETCH r.receiver rec
		    JOIN FETCH rec.user
		    WHERE rec.user.username = :username
		    AND r.status = :status
		    ORDER BY r.requestDate DESC
		""")
		List<SwapRequest> getPendingRequests(
		    @Param("username") String username,
		    @Param("status") String status
		);
	
	


	    Long countByStatus(String status);
}
