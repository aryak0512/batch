package org.aryak.batch.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class FlowerListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("FlowerListener : before step");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        System.out.println("params :" + stepExecution.getJobParameters().getParameters());
        var flowerType = stepExecution.getJobParameters().getString("type", "roses");
        assert flowerType != null;
        return flowerType.equals( "roses" ) ? new ExitStatus("TRIM_REQUIRED") : new ExitStatus("NO_TRIM_REQUIRED");
    }
}
