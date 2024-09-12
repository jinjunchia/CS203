package com.cs203.cs203system.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }
    public NotFoundException(String message) {
        super(message);
    }
}
