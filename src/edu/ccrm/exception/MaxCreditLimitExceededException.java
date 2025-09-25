package edu.ccrm.exception;

public class MaxCreditLimitExceededException extends Exception {

	private static final long serialVersionUID = -4698570712242276234L;

	public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}