package com.skwarek.blogger.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }

}
