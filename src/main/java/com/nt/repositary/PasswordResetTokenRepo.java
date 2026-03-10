package com.nt.repositary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.model.PasswordResetToken;
import com.nt.model.Users;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> 
{
	PasswordResetToken findByToken(String token);

	Optional<PasswordResetToken> findByUser(Users user);
}
