package com.skwarek.blogger.exception;

public class NotFoundCommentException extends RuntimeException {

    public NotFoundCommentException(String message) {
        super(message);
    }

}
