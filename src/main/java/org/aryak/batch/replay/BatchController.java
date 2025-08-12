package org.aryak.batch.replay;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job rerunFailedLinesJob;

    @Autowired
    private FailedLineRepository failedLineRepository;
    private final String fileName = "/Users/aryak/Downloads/batch-jobs/src/main/resources/employees.csv";

    @GetMapping("/rerun")
    public ResponseEntity<Map<String, Object>> rerunFailedLines() {
        try {
            // Validate that there are failed lines to process
//            List<Integer> failedLines = failedLineRepository.findUnprocessedLineNumbers(request.getFileName());
//            if (failedLines.isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "No failed lines found for file: " + request.getFileName()));
//            }
            List<Integer> failedLines = List.of(1,3,5);
            // Create job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("input.file.name", fileName)
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("job.type", "rerun")
                    .toJobParameters();

            // Launch the job
            JobExecution jobExecution = jobLauncher.run(rerunFailedLinesJob, jobParameters);

            Map<String, Object> response = new HashMap<>();
            response.put("jobExecutionId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("failedLinesCount", failedLines.size());
            response.put("message", "Rerun job started successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to start rerun job: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/job-status/{jobExecutionId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long jobExecutionId) {
        // Implementation to get job status
        // You can use your existing job status checking logic here
        return ResponseEntity.ok(Map.of("status", "RUNNING")); // Placeholder
    }
}

