package org.aryak.batch.replay;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DataItemProcessor implements ItemProcessor<String, ProcessedData> {

    @Override
    public ProcessedData process(String line) throws Exception {

        System.out.println("Processing line: " + line);
        return new ProcessedData(line); // Replace with your actual processing
    }
}
