package io.github.job.scheduler.repository;

import io.github.job.scheduler.entity.LogsJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsJobRepository extends JpaRepository<LogsJob, Long> {
}
