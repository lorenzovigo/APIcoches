package com.lorenzovigo.api.exceptions;

public class InvalidSortValueException extends RuntimeException {
    public InvalidSortValueException(String value) {
        super("'" + value + "' no es un orden correcto. Las opciones válidas para el parámetro sort son: IA, ID, VA, VD.");
    }
}
