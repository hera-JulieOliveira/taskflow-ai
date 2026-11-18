package com.taskflow.taskflow.repository;

import com.taskflow.taskflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}
