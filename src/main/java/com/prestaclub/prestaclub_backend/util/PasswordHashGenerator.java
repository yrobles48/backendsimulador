package com.prestaclub.prestaclub_backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String plainPassword = "M@r@cuy@1125";
        String hashedPassword = passwordEncoder.encode(plainPassword);
        
        System.out.println("=========================================");
        System.out.println("Password: " + plainPassword);
        System.out.println("Hash: " + hashedPassword);
        System.out.println("=========================================");
        System.out.println("\nSQL para actualizar:");
        System.out.println("UPDATE usuarios SET password = '" + hashedPassword + "' WHERE username = 'JHUAMAN';");
        System.out.println("=========================================");
        
        // Verificar
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        System.out.println("\nVerificación - Matches: " + matches);
        
        // Verificar con el hash viejo
        String oldHash = "$2a$10$yQb3IPRzk0n0EOm3tr1r6uTN1pqyvareZDDEyfPFkSxpATeIXBuBe";
        System.out.println("\nOld hash matches 'M@r@cuy@1125': " + passwordEncoder.matches(plainPassword, oldHash));
    }
}