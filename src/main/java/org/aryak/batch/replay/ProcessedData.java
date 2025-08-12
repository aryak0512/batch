package org.aryak.batch.replay;

import lombok.Data;

@Data
public class ProcessedData {

    private String data;
    private int originalLineNumber;

    public ProcessedData(String data) {
        this.data = data;
        // You'll need to track line numbers in your processor
    }

}
