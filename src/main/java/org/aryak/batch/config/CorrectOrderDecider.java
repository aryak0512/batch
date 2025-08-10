package org.aryak.batch.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class CorrectOrderDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        // an order is correct 70% of the time
        return new FlowExecutionStatus("CORRECT");

//        if (Math.random() < 0.7) {
//            return new FlowExecutionStatus("CORRECT");
//        }
//        return new FlowExecutionStatus("INCORRECT");

    }
}
