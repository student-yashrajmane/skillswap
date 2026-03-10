package com.nt.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nt.model.ActivitLogMessage;

public interface IActivityLogRepo extends JpaRepository<ActivitLogMessage, Long>
{
	List<ActivitLogMessage> findAllByOrderByIdDesc();
}
