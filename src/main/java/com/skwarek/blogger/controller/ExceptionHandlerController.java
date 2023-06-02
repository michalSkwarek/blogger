package com.skwarek.blogger.controller;

import com.skwarek.blogger.exception.DuplicateUserException;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.exception.NotFoundUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> userNotFound() {
        String error = "This user doesn't exist.";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> userExisting() {
        String error = "This user already exists.";

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
