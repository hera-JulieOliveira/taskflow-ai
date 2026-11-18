package com.taskflow.taskflow.dto;

import com.taskflow.taskflow.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String title,

        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String description,

        TaskStatus status
) {}
