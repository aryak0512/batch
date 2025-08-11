package org.aryak.batch.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCustomException extends RuntimeException {

    private final String message;

    public MyCustomException(String message) {
        super(message);
        this.message = message;
    }
}
