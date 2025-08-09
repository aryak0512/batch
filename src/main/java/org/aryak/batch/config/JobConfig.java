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
                .next(driveToAddressStep())
                .next(givePackageToCustomerStep())
                //.incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step packageItemStep() {
        return new StepBuilder("packageItemStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("The item has been packaged.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step driveToAddressStep() {

        boolean GOT_LOST = false; // Simulating a condition where the driver might get lost

        return new StepBuilder("driveToAddressStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    if (GOT_LOST) {
                        throw new RuntimeException("Got lost driving to the address.");
                    }

                    System.out.println("Successfully arrived at the address.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step givePackageToCustomerStep() {
        return new StepBuilder("givePackageToCustomerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Given the package to the customer.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
