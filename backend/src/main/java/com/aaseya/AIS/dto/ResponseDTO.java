package com.aaseya.AIS.dto;

public class ResponseDTO {
	
	private String status;
	private String message;
	private String errorCode;
	private String errorMessage;
	private String ChatGPTResponse;
	private String businessKey;
	private String notificationName;
	private String notificationMessage;

	
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
	public String getChatGPTResponse() {
		return ChatGPTResponse;
	}
	public void setChatGPTResponse(String chatGPTResponse) {
		ChatGPTResponse = chatGPTResponse;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getNotificationName() {
		return notificationName;
	}
	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}
	public String getNotificationMessage() {
		return notificationMessage;
	}
	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}
	
	

}
