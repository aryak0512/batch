package org.aryak.batch.controller;

import lombok.RequiredArgsConstructor;
import org.aryak.batch.model.Order;
import org.aryak.batch.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.aryak.batch.config.EmployeeJobConfig.FILE_PATH;

@RequiredArgsConstructor
@RestController
public class JobController {

    private final JobService jobService;
    private final JobLauncher jobLauncher;
    private final Job employeeJob;

    @GetMapping
    public void m1() {
        jobService.m1();
    }

    @GetMapping("/m2")
    public void m2() {
        jobService.m2();
    }

    @GetMapping("/m3")
    public Order m3() {
        return jobService.m3();
    }

    @GetMapping("/run")
    public String runBau() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", FILE_PATH)
                .addString("mode", "BAU")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(employeeJob, params);
        return "BAU job started for " + FILE_PATH;
    }

    @GetMapping("/rerun")
    public String rerunFailed() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", FILE_PATH)
                .addString("mode", "RERUN")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(employeeJob, params);
        return "Rerun job started for " + FILE_PATH;
    }

}
