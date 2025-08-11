package org.aryak.batch.config;

import lombok.RequiredArgsConstructor;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

@RequiredArgsConstructor
@Configuration
public class SampleScheduledJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobExplorer jobExplorer;

    @Bean
    public Job sampleJob() {
        return new JobBuilder("sample-job", jobRepository)
                .start(sampleStep())
                .build();
    }

    @Bean
    public Step sampleStep() {
        return new StepBuilder("sample-step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step executed at : " + new Date());
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }


    //@Bean
    public Trigger myJobTrigger() {
        return TriggerBuilder.newTrigger()
                //.forJob(myJobDetail())
                .withIdentity("myQuartzTrigger")
                // Runs every 10 seconds
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
    }
}
