package io.github.job.scheduler.entity.dto;

public record JobResponseDTO(
        String name,
        String description,
        String cronExpression,
        String cronDescription
) {
}
