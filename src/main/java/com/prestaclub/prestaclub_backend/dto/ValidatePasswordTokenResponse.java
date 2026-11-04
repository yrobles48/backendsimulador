package com.prestaclub.prestaclub_backend.dto;

public class ValidatePasswordTokenResponse {
    private boolean valid;
    private String mensaje;
    private String expiracion;

    public ValidatePasswordTokenResponse(boolean valid, String mensaje, String expiracion) {
        this.valid = valid;
        this.mensaje = mensaje;
        this.expiracion = expiracion;
    }


    public ValidatePasswordTokenResponse() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(String expiracion) {
        this.expiracion = expiracion;
    }
}
