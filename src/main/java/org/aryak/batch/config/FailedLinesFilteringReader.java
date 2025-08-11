package org.aryak.batch.config;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FailedLinesFilteringReader<T> implements ItemStreamReader<T> {

    private final FlatFileItemReader<T> delegate;
    private final Set<Integer> failedLines;
    private int currentLine = 0;

    public FailedLinesFilteringReader(FlatFileItemReader<T> delegate, List<Integer> failedLines) {
        this.delegate = delegate;
        this.failedLines = new HashSet<>(failedLines);
    }

    @Override
    public T read() throws Exception {
        T item;
        while ((item = delegate.read()) != null) {
            currentLine++;
            if (failedLines.contains(currentLine)) {
                System.out.println("Returning failed line: " + currentLine);
                return item; // Only return if line is in failed list
            }
        }
        return null; // End of file
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}