package io.github.job.scheduler.service;

import io.github.job.scheduler.entity.LogsJob;
import io.github.job.scheduler.repository.LogsJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogsJobService {

    @Autowired
    private LogsJobRepository logsJobRepository;

    public LogsJob createLog(LogsJob log) {
        return logsJobRepository.save(log);
    }
}
