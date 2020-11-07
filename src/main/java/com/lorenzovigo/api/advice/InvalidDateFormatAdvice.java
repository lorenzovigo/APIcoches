package com.lorenzovigo.api.advice;

import com.lorenzovigo.api.exceptions.InvalidDateFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidDateFormatAdvice {
    @ResponseBody
    @ExceptionHandler(InvalidDateFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidDateFormatHandler(InvalidDateFormatException ex) {
        return ex.getMessage();
    }
}
