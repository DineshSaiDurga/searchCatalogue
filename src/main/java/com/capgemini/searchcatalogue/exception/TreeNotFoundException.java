package com.capgemini.searchcatalogue.exception;

public class TreeNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TreeNotFoundException() {
		super();
	}

	public TreeNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TreeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TreeNotFoundException(String message) {
		super(message);
	}

	public TreeNotFoundException(Throwable cause) {
		super(cause);
	}

	
}
