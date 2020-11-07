package com.lorenzovigo.api.advice;

import com.lorenzovigo.api.exceptions.InvalidSortValueException;
import com.lorenzovigo.api.exceptions.NonUniqueValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidSortValueAdvice {

    @ResponseBody
    @ExceptionHandler(InvalidSortValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidSortValueHandler(InvalidSortValueException ex) {
        return ex.getMessage();
    }
}