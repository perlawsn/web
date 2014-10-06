package org.dei.perla.rest.controller;

public class PerLaException extends Exception {

	private static final long serialVersionUID = 6573111658596764651L;

	public PerLaException() {
		super();
	}
	
	public PerLaException(String message) {
		super(message);
	}
	
	public PerLaException(Throwable cause) {
		super(cause);
	}
	
	public PerLaException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
