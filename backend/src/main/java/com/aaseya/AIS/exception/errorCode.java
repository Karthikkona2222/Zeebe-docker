package com.aaseya.AIS.exception;
 
import org.springframework.http.HttpStatus;
 
public enum errorCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");
 
    private final HttpStatus status;
    private final String code;
    private final String message;
 
    errorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
 
    public HttpStatus getStatus() {
        return status;
    }
 
    public String getCode() {
        return code;
    }
 
    public String getMessage() {
        return message;
    
    }}
 