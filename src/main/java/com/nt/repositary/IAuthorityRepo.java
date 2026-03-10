package com.nt.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nt.model.Authority;
import com.nt.model.Users;

public interface IAuthorityRepo extends JpaRepository<Authority, Long> {
	@Query("SELECT u FROM Users u JOIN u.authority r WHERE r.authority = :role")
	List<Users> findUsersByAuthority(@Param("role") String role);
}
