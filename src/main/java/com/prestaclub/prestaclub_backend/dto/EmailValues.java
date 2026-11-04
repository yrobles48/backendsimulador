package com.prestaclub.prestaclub_backend.dto;

public class EmailValues { 
    private String email;
    private String subject;
    private String username;
    private String jwt;

    public EmailValues() {}

    public EmailValues(String email, String subject, String username, String jwt) { 
        this.email = email;
        this.subject = subject;
        this.username = username;
        this.jwt = jwt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
 
  

    
}
