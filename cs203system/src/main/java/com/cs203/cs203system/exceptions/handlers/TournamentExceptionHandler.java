package com.cs203.cs203system.exceptions.handlers;

import com.cs203.cs203system.exceptions.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice(assignableTypes = {TournamentController.class})
public class TournamentExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleTournamentNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
