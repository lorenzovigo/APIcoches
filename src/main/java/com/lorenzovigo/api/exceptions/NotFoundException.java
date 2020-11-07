package com.lorenzovigo.api.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, String variable, String value) {
        super("No se ha encontrado una instancia del tipo " + entity + " con " + variable + " de valor '" + value + "'.");
    }
}
