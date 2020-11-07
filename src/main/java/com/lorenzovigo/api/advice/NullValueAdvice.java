package com.lorenzovigo.api.advice;

import com.lorenzovigo.api.exceptions.NonUniqueValueException;
import com.lorenzovigo.api.exceptions.NullValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NullValueAdvice {

    @ResponseBody
    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String nullValueHandler(NullValueException ex) {
        return ex.getMessage();
    }
}