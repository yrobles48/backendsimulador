package com.prestaclub.prestaclub_backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ValidatePasswordTokenRequest {
    @NotBlank(message = "El token no debe estar vacío")
    private String token;

    public ValidatePasswordTokenRequest(String token) {
        this.token = token;
    }

    public ValidatePasswordTokenRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
