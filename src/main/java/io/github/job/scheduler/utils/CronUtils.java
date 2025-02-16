package io.github.job.scheduler.utils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.util.Optional;

public class CronUtils {

    public static String getCronDate(String cronExpression) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
        Cron cron = parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(ZonedDateTime.now());
        return nextExecution.map(ZonedDateTime::toString).orElse("No next execution time found");
    }

    public static boolean isValidCronExpression(String cronExpression) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
        try {
            parser.parse(cronExpression).validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
