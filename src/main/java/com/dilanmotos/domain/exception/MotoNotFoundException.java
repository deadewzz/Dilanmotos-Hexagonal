package com.dilanmotos.domain.exception;

public class MotoNotFoundException extends RuntimeException {

    public MotoNotFoundException(String message) {
        super(message);
    }
}
