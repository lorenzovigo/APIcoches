package com.lorenzovigo.api.exceptions;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String variable, String value) {
        super("'" + value + "' no es un valor válido para la variable '" + variable +"'.");
    }
}
