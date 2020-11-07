package com.lorenzovigo.api.advice;

import com.lorenzovigo.api.exceptions.InvalidActionException;
import com.lorenzovigo.api.exceptions.InvalidValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidActionAdvice {

    @ResponseBody
    @ExceptionHandler(InvalidActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidActionHandler(InvalidActionException ex) {
        return ex.getMessage();
    }
}