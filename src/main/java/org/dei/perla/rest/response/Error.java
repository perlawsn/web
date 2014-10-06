package org.dei.perla.rest.response;

public class Error {

	private static final boolean error = true;
	private String description;
	
	public Error(String description) {
		this.description = description;
	}
	
	public Error(Throwable cause) {
		this.description = cause.getMessage();
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getDescription() {
		return description;
	}
	
}
