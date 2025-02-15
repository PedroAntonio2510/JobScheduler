package io.github.job.scheduler.entity;

import io.github.job.scheduler.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_job_logs")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Logs_Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private LocalDateTime executionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String error_message;

}
