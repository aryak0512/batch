package org.aryak.batch.replay;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingItemWriter implements ItemWriter<ProcessedData> {

    @Autowired
    private FailedLineRepository failedLineRepository;

    @Override
    public void write(Chunk<? extends ProcessedData> chunk) throws Exception {
        // Mark lines as processed
        for (ProcessedData item : chunk) {

            System.out.println("Marking line as processed: " + item.getOriginalLineNumber());
            //failedLineRepository.markAsProcessed("fileName", item.getOriginalLineNumber());
        }
    }
}
