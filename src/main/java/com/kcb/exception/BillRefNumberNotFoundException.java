package com.kcb.exception;

public class BillRefNumberNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public BillRefNumberNotFoundException(String errorMessage) {
		super(errorMessage);
	}
	
	public BillRefNumberNotFoundException(Exception e) {
		super(e);
	}

}
