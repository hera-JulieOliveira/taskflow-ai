package com.taskflow.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AiFeedbackRequest(
        @NotBlank(message = "A funcionalidade é obrigatória")
        @Pattern(regexp = "description|subtasks|summary", message = "Funcionalidade inválida")
        String feature,

        @NotNull(message = "Informe se foi útil")
        Boolean helpful,

        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        String comment
) {}
