package org.aryak.batch.config;

import org.aryak.batch.listeners.FlowerListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
    public JobExecutionDecider decider() {
        return new DeliveryDecider();
    }

    @Bean
    public JobExecutionDecider correctOrderDecider() {
        return new CorrectOrderDecider();
    }

    @Bean
    public Job prepareFlowersJob() {
        return new JobBuilder("prepareFlowersJob", jobRepository)
                .start(gatherFlowersStep())
                    .on("TRIM_REQUIRED").to(removeThornsStep()).next(arrangeFlowersStep())
                .from(gatherFlowersStep()).on("NO_TRIM_REQUIRED").to(arrangeFlowersStep())
                .end()
                .build();
    }

    @Bean
    public Job deliverPackageJob() {
        return new JobBuilder("deliverPackageJob", jobRepository)
                .start(packageItemStep())
                .next(driveToAddressStep())
                    .on("FAILED").stop()
                .from(driveToAddressStep())
                    .on("*").to(decider())
                        .on("PRESENT").to(givePackageToCustomerStep())
                            .next(correctOrderDecider()).on("CORRECT").to(thankCustomerStep())
                            .from(correctOrderDecider()).on("INCORRECT").to(refundStep())
                        .from(decider()).on("NOT_PRESENT").to(leaveAtDoorStep())
                .end()
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

    @Bean
    public Step storePackageStep() {
        return new StepBuilder("storePackageStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Storing the package while customer address is located.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step leaveAtDoorStep() {
        return new StepBuilder("leaveAtDoorStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Leaving the package at the door.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step thankCustomerStep() {
        return new StepBuilder("thankCustomerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Thanking the customer.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step refundStep() {
        return new StepBuilder("refundStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Processing a refund.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step gatherFlowersStep() {
        return new StepBuilder("gatherFlowersStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Preparing flowers.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .listener(new FlowerListener())
                .build();
    }

    @Bean
    public Step arrangeFlowersStep() {
        return new StepBuilder("arrangeFlowersStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Arranging flowers for order.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step removeThornsStep() {
        return new StepBuilder("removeThornsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Removing thorns from roses.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
