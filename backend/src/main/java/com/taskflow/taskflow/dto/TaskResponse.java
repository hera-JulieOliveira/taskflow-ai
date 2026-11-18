package com.taskflow.taskflow.dto;

import com.taskflow.taskflow.model.Task;
import com.taskflow.taskflow.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, String description,
                           TaskStatus status, Long projectId, LocalDateTime createdAt) {

    public static TaskResponse from(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getStatus(), t.getProject().getId(), t.getCreatedAt());
    }
}
