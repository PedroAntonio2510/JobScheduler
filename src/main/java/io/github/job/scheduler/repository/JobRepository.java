package io.github.job.scheduler.repository;

import io.github.job.scheduler.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByActivateTrue();
}
