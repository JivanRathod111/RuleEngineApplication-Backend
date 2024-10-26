package com.example.demo.Exception;

public class ConditionNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConditionNotFoundException(String message) {
        super(message);
    }
}
