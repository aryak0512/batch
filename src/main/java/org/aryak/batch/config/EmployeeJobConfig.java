package org.aryak.batch.config;

import lombok.RequiredArgsConstructor;
import org.aryak.batch.exceptions.MyCustomException;
import org.aryak.batch.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class EmployeeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public static final String FILE_PATH =
            "/Users/aryak/Downloads/batch-jobs/src/main/resources/employees.csv";

    @Bean
    public Job employeeJob(Step bauStep, Step rerunStep) {
        return new JobBuilder("employee", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(bauStep)       // you can choose bauStep or rerunStep dynamically in your REST controller
                //.next(rerunStep)      // optional — or remove if you only want one step at a time
                .build();
    }

    // BAU step
    @Bean
    public Step bauStep(ItemReader<Employee> bauReader,
                        ItemProcessor<Employee, Employee> processor,
                        ItemWriter<Employee> writer) {
        return new StepBuilder("bauStep", jobRepository)
                .<Employee, Employee>chunk(5, transactionManager)
                .reader(bauReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(MyCustomException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(new SkipListener<>() {
                    @Override
                    public void onSkipInProcess(Employee item, Throwable t) {
                        // Save failed line numbers to DB here
                        System.out.println("Skipped: " + item);
                    }
                })
                .build();
    }

    // Rerun step
    @Bean
    public Step rerunStep(ItemStreamReader<Employee> rerunReader,
                          ItemProcessor<Employee, Employee> processor,
                          ItemWriter<Employee> writer) {
        return new StepBuilder("rerunStep", jobRepository)
                .<Employee, Employee>chunk(5, transactionManager)
                .reader(rerunReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // BAU reader
    @Bean
    @StepScope
    public FlatFileItemReader<Employee> bauReader() {
        return new FlatFileItemReaderBuilder<Employee>()
                .name("BauEmployeeReader")
                .resource(new FileSystemResource(FILE_PATH))
                .delimited()
                .delimiter(",")
                .names("id", "name", "location", "email")
                .targetType(Employee.class)
                .build();
    }

    // Rerun reader — now as a managed bean
    @Bean
    @StepScope
    public ItemStreamReader<Employee> rerunReader() {
        FlatFileItemReader<Employee> baseReader =
                new FlatFileItemReaderBuilder<Employee>()
                        .name("RerunEmployeeReader")
                        .resource(new FileSystemResource(FILE_PATH))
                        .delimited()
                        .delimiter(",")
                        .names("id", "name", "location", "email")
                        .targetType(Employee.class)
                        .build();

        List<Integer> failedLines = List.of(1, 5, 3); // load from DB in real scenario
        return new FailedLinesFilteringReader<>(baseReader, failedLines);
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor() {
        return e -> {
            if ("Glasgow".equalsIgnoreCase(e.getLocation())) {
                throw new MyCustomException("Invalid data: " + e);
            }
            return e;
        };
    }

    @Bean
    public ItemWriter<Employee> writer() {
        return items -> items.forEach(System.out::println);
    }
}