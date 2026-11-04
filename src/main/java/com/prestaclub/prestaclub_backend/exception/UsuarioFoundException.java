package com.prestaclub.prestaclub_backend.exception;

public class UsuarioFoundException extends Exception {

    public UsuarioFoundException() {
        super("El usuario ya existe");
    } 

    public UsuarioFoundException(String message) {
        super(message);
    }
}
