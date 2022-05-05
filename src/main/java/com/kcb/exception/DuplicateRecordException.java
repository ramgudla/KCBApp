package com.kcb.exception;

public class DuplicateRecordException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateRecordException(String errorMessage) {
		super(errorMessage);
	}
	
	public DuplicateRecordException(Exception e) {
		super(e);
	}

}
