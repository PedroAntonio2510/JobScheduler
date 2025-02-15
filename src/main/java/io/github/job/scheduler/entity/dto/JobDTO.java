package io.github.job.scheduler.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobDTO(
        @NotNull
        @NotBlank
        String name,

        String description,

        @NotNull
        @NotBlank
        String cronExpression
) {
}
