package io.github.job.scheduler.mapper;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.dto.JobDTO;
import io.github.job.scheduler.entity.dto.JobResponseDTO;
import io.github.job.scheduler.utils.CronUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = CronUtils.class)
public interface JobMapper {

    Job toJob(JobDTO jobDTO);

    @Mapping(target = "cronDescription", expression = "java( CronUtils.describeCron(job.getCronExpression()) )")
    JobResponseDTO toJobResponseDTO(Job job);

//    default String convertCronToDescription(String cronExpression) {
//
//        String cronDescription = CronUtils.describeCron(cronExpression);
//
//        return cronDescription;
//    }
}
