package com.example.demo.exception;

public class InvalidMenuStateException extends RuntimeException {

    public InvalidMenuStateException(String message) {
        super(message);
    }
}