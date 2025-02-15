package io.github.job.scheduler.mapper;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.dto.JobDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    Job toJob(JobDTO jobDTO);

    JobDTO toJobDTO(Job job);
}
