package com.skwarek.blogger.exception;

public class NotFoundPostException extends RuntimeException {

    public NotFoundPostException(String message) {
        super(message);
    }

}
