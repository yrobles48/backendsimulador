package com.prestaclub.prestaclub_backend.dto;

public class ChangePasswordValues {
 
    private String newPassword;
    private String token;
    

    public ChangePasswordValues() {}

    public ChangePasswordValues(String newPassword, String token) {
        this.newPassword = newPassword;
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
