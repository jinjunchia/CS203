package com.cs203.cs203system.exceptions;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException() {
        super();
    }
    public TournamentNotFoundException(String message) {
        super(message);
    }
}
