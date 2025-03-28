package com.mi.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UserException.class)
    @ResponseBody
    public ResponseEntity<?> invalidUserRequestHandler(UserException e) {
        return ResponseEntity
                .status(400)
                .body(e.getMessage());
    }
}
