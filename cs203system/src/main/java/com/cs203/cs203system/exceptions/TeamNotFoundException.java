package com.cs203.cs203system.exceptions;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException() {
        super();
    }
    public TeamNotFoundException(String message) {
        super(message);
    }
}
