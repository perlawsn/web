package org.dei.perla.rest;

public class Result {

	private String status;
	private String message;
	
	public Result(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public Result(Throwable cause) {
		this.status = "error";
		this.message = cause.getMessage();
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
	
}
