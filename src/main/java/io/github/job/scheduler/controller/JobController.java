package io.github.job.scheduler.controller;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.dto.JobDTO;
import io.github.job.scheduler.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    public ResponseEntity<Job> saveJob(@RequestBody @Valid JobDTO data) {
        jobService.createJob(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<String> updateJob(@PathVariable Long jobId,
                                         @RequestBody @Valid JobDTO data) {
        Job jobFound = jobService.getJobById(jobId);
        jobFound.setName(data.name());
        jobFound.setDescription(data.description());
        jobFound.setCronExpression(data.cronExpression());

        jobService.updateJob(jobFound);

        return ResponseEntity.ok("Sua tarefa foi modifica de horario " + jobFound.getCronExpression());
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId){
        jobService.stopJob(jobId);

        return ResponseEntity.noContent().build();
    }

}
