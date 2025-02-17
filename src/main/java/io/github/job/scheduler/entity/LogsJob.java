package io.github.job.scheduler.entity;

import io.github.job.scheduler.entity.enums.STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_logs_jobs")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogsJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private String executionDate;

    @Enumerated(EnumType.STRING)
    private STATUS status;

    private String errorMessage;

}
