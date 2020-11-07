package com.lorenzovigo.api.exceptions;

public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String motive) {
        super("No se puede realizar esta acción " + motive);
    }
}
