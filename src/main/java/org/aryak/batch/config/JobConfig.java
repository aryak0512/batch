package org.aryak.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public JobConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Job deliverPackageJob() {
        return new JobBuilder("deliverPackageJob", jobRepository)
                .start(packageItemStep())
                .build();
    }

    @Bean
    public Step packageItemStep() {
        return new StepBuilder("packageItemStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Packaging items...");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
