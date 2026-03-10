package com.nt.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.model.QuizDetails;

public interface IQuizDetailsRepo extends JpaRepository<QuizDetails, Long> {

}
