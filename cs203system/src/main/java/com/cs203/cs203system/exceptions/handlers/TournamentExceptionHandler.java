package com.cs203.cs203system.exceptions.handlers;

import com.cs203.cs203system.controller.TournamentController;
import com.cs203.cs203system.exceptions.TournamentNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(1)
@ControllerAdvice(assignableTypes = {TournamentController.class})
public class TournamentExceptionHandler {
    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<String> handleTournamentNotFoundException(TournamentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
