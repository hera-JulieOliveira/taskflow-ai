package com.taskflow.taskflow.repository;

import com.taskflow.taskflow.model.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
}
