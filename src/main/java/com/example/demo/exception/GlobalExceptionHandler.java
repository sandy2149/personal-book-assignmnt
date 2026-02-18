package com.example.demo.exception;

import com.example.demo.constants.ErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Global exception handler for application-level errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles CustomBusinessException and returns 400 Bad Request.
     *
     * @param ex CustomBusinessException
     * @return error message with HTTP 400
     */
    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<String> handleCustomBusinessException(CustomBusinessException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Fallback handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorConstants.UNEXPECTED_ERROR);
    }
}

