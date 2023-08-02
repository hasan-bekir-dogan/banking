package com.banking.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CheckAmountException extends RuntimeException implements Serializable {
    private static final Long serialVerisonUID = 1L;

    public CheckAmountException(String message) {
        super(message);
    }
}
