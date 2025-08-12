package org.aryak.batch.replay;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@StepScope
public class SelectiveLineItemReader implements ItemReader<String> {

    private BufferedReader bufferedReader;
    private Set<Integer> targetLineNumbers;
    private int currentLineNumber = 0;
    private Iterator<Integer> targetLineIterator;
    private Integer nextTargetLine;
    private final String fileName = "/Users/aryak/Downloads/batch-jobs/src/main/resources/employees.csv";

    @Autowired
    private FailedLineRepository failedLineRepository;

    @PostConstruct
    public void init() throws IOException {
        // Get failed line numbers for this file
        //List<Integer> failedLines = failedLineRepository.findUnprocessedLineNumbers(fileName);
        List<Integer> failedLines = List.of(1, 3, 5);
        targetLineNumbers = new TreeSet<>(failedLines);
        targetLineIterator = targetLineNumbers.iterator();
        nextTargetLine = targetLineIterator.hasNext() ? targetLineIterator.next() : null;

        // Initialize file reader
        Resource resource = new FileSystemResource(fileName);
        bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
    }

    @Override
    public String read() throws Exception {

        System.out.println("nextTargetLine = " + nextTargetLine);
        if (nextTargetLine == null) {
            return null; // No more lines to process
        }

        String line = null;

        // Skip lines until we reach the target line number
        while (currentLineNumber < nextTargetLine) {
            line = bufferedReader.readLine();
            currentLineNumber++;
            if (line == null) {
                return null; // End of file reached
            }
        }

        // We're at the target line, prepare for next target
        if (currentLineNumber == nextTargetLine) {
            nextTargetLine = targetLineIterator.hasNext() ? targetLineIterator.next() : null;
            return line;
        }

        return null;
    }

    public void close() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // Log error
            }
        }
    }
}
