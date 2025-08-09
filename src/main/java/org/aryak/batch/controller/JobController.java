package org.aryak.batch.controller;

import lombok.RequiredArgsConstructor;
import org.aryak.batch.model.Order;
import org.aryak.batch.service.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JobController {

    private final JobService jobService;

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
}
