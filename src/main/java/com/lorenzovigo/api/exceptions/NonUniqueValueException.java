package com.lorenzovigo.api.exceptions;

public class NonUniqueValueException extends RuntimeException {
    public NonUniqueValueException(String variable, String value) {
        super("La variable '" + variable + "' necesita un valor único y el valor '" + value + "' ya está en uso.");
    }
}
