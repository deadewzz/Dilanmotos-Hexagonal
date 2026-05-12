package com.dilanmotos.domain.exception;

public class PqrsNotFoundException extends RuntimeException {

    public PqrsNotFoundException(String message) {
        super(message);
    }
}