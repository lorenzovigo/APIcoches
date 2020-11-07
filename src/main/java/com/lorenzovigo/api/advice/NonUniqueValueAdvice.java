package com.lorenzovigo.api.advice;

import com.lorenzovigo.api.exceptions.NonUniqueValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NonUniqueValueAdvice {

    @ResponseBody
    @ExceptionHandler(NonUniqueValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String nonUniqueValueHandler(NonUniqueValueException ex) {
        return ex.getMessage();
    }
}