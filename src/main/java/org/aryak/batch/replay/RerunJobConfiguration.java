package org.aryak.batch.replay;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class RerunJobConfiguration {

    @Autowired
    private SelectiveLineItemReader selectiveLineItemReader;

    @Autowired
    private DataItemProcessor dataItemProcessor;

    @Autowired
    private TrackingItemWriter trackingItemWriter;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job rerunFailedLinesJob() {

        return new JobBuilder("rerunFailedLinesJob", jobRepository)
                .start(rerunFailedLinesStep())
                .build();
    }

    @Bean
    public Step rerunFailedLinesStep() {

        return new StepBuilder("rerunFailedLinesStep", jobRepository)
                .<String, ProcessedData>chunk(10, platformTransactionManager)
                .reader(selectiveLineItemReader)
                .processor(dataItemProcessor)
                .writer(trackingItemWriter)
                .faultTolerant()
                .build();
    }

}
