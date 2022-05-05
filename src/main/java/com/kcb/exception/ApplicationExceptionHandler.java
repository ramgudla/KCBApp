package com.kcb.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(BillRefNumberNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(BillRefNumberNotFoundException e) {
    	Map<String, String> errorResponse = new LinkedHashMap<String, String>();
    	errorResponse.put("timestamp", Instant.now().toString());
    	errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
    	errorResponse.put("error", "KFS Validation Error");
        errorResponse.put("message", e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<Map<String, String>> handleException(DuplicateRecordException e) {
    	Map<String, String> errorResponse = new LinkedHashMap<String, String>();
    	errorResponse.put("timestamp", Instant.now().toString());
    	errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
    	errorResponse.put("error", "KFS Validation Error");
        errorResponse.put("message", e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
