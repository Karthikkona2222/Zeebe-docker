package com.aaseya.AIS.exception;
 
public class errorResponse {
    private String status;
    private String message;
    private String errorCode;
    private String errorMessage;
 
    public errorResponse(errorCode errorCode) {
        this.status = errorCode.getStatus().toString();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
 
    
public String getStatus() {
		return status;
	}
 
 
	public void setStatus(String status) {
		this.status = status;
	}
 
 
	public String getMessage() {
		return message;
	}
 
 
	public void setMessage(String message) {
		this.message = message;
	}
 
 
	public String getErrorCode() {
		return errorCode;
	}
 
 
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
 
 
	public String getErrorMessage() {
		return errorMessage;
	}
 
 
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
 
 
{
 
}}