package com.prestaclub.prestaclub_backend.exception;

public class UsuarioNotFoundException extends Exception {
    public UsuarioNotFoundException() {
        super("El usuario no existe");
    } 

    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
