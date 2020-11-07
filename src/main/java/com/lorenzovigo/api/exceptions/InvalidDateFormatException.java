package com.lorenzovigo.api.exceptions;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String date) {
        super("'" + date + "' no cumple con el formato de fecha necesario: yyyy-MM-dd HH:mm:ss");
    }
}