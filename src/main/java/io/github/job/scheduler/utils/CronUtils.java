package io.github.job.scheduler.utils;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;


import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.cronutils.model.definition.CronDefinitionBuilder.instanceDefinitionFor;

public class CronUtils {

    public static String getCronDate(String cronExpression) {
        CronParser parser = new CronParser(instanceDefinitionFor(CronType.SPRING));
        Cron cron = parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(ZonedDateTime.now());
        return nextExecution.map(ZonedDateTime::toString).orElse("No next execution time found");
    }

    public static String describeCron(String cronExpression) {
        try {
            CronDefinition cronDefinition = instanceDefinitionFor(CronType.SPRING);
            CronParser parser = new CronParser(cronDefinition);
            CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
            String description = descriptor.describe(parser.parse(cronExpression));

            return description;
        } catch (Exception e) {
            return "Expressao cron invalida";
        }
    }

    public static boolean isValidCronExpression(String cronExpression) {
        CronParser parser = new CronParser(instanceDefinitionFor(CronType.SPRING));
        try {
            parser.parse(cronExpression).validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
