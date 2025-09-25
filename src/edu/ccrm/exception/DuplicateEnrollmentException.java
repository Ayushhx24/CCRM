package edu.ccrm.exception;

public class DuplicateEnrollmentException extends Exception {

	private static final long serialVersionUID = 6702382283090293703L;

	public DuplicateEnrollmentException(String message) {
        super(message);
    }
}