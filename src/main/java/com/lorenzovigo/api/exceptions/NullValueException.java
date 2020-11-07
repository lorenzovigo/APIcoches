package com.lorenzovigo.api.exceptions;

public class NullValueException extends RuntimeException {
    public NullValueException(String variable) {
        super("La variable '" + variable + "' no puede tener un valor null.");
    }
}
