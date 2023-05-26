package com.skwarek.blogger.exception;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(String message) {
        super(message);
    }

}
