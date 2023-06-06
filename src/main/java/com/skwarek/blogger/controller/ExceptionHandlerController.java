package com.skwarek.blogger.controller;

import com.skwarek.blogger.exception.DuplicateAccountException;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.exception.NotFoundAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(NotFoundAccountException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> userNotFound() {
        String error = "This account doesn't exist.";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateAccountException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> userExisting() {
        String error = "This account already exists.";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundPostException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> postNotFound() {
        String error = "This post doesn't exist.";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NotFoundCommentException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> commentNotFound() {
        String error = "This comment doesn't exist.";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
