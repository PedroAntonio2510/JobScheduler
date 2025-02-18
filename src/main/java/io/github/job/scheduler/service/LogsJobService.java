package io.github.job.scheduler.service;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.LogsJob;
import io.github.job.scheduler.repository.JobRepository;
import io.github.job.scheduler.repository.LogsJobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogsJobService {

    @Autowired
    private LogsJobRepository logsJobRepository;

    @Autowired
    private JobRepository jobRepository;

    public void createLog(Long jobId, String mensagem) {
        // Buscar o Job no banco para garantir que está gerenciado
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job não encontrado!"));

        LogsJob log = new LogsJob();
        log.setJob(job);
        log.setErrorMessage(mensagem);

        logsJobRepository.save(log);
    }

}
