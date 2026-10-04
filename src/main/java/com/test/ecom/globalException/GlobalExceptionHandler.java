package com.test.ecom.globalException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  Catch Global Exceptions
     *  Used ExceptionHandler(custom/Exception.class) ... Your Return method .
     * Log Initiator
     * Return  Response Entity. status . body */


    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /* Random Exception Handler */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFound(
            NoResourceFoundException ex) {

        log.warn("Resource not found: {}", ex.getResourcePath());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Resource not found");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {

                    if (error.isBindingFailure()) {
                        return "Invalid value for " + error.getField();
                    }

                    return error.getDefaultMessage();
                })
                .toList();

        log.error("validation error : {}", messages);

        return ResponseEntity
                .badRequest()
                .body(messages);
    }


   // FOR sql related exceptions and database related .
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDatabaseException(
            DataAccessException ex) {

        log.error("|| Database error ||", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to process the request at this time.");
    }


  /*  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return ResponseEntity
                .badRequest()
                .body("Invalid value for " + ex.getName());
    }

   */



}
