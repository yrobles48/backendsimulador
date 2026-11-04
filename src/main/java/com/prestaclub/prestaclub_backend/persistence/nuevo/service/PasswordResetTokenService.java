package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ValidatePasswordTokenResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.PasswordResetToken;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.PasswordResetTokenRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class PasswordResetTokenService {
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;  
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PasswordResetToken findByToken(String token) { 
        return passwordResetTokenRepository.findByToken(token);
    }
    
    public void delete(PasswordResetToken resetToken) {
        passwordResetTokenRepository.delete(resetToken); 
    }

    @Transactional
    public boolean validateToken(String token) { 
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken != null) { 
            return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Transactional
    public ValidatePasswordTokenResponse validatePasswordToken(String token) { 
        ValidatePasswordTokenResponse response = new ValidatePasswordTokenResponse();  
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token); 
        if (resetToken != null) { 
            if (!resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                response.setValid(false);
                response.setMensaje("El token ha expirado");
            }else{
                response.setValid(true);
                response.setExpiracion(resetToken.getExpiryDate().toString());
                response.setMensaje("El token es valido");
            }
        }else{
            response.setValid(false);
            response.setMensaje("El token no Existe");
        }
        return response;
    }


    @Transactional
    public boolean changePasswordByToken(String token, String newPassword) { 
        try { 
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
            if (resetToken != null) {  
                Usuario usuario = resetToken.getUsuario();
                if (!changePassword(usuario, newPassword)) {
                    return false;
                }
                passwordResetTokenRepository.delete(resetToken); 
                return true; 
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cambiarContra(Usuario usuario, String newPassword) { 
       return changePassword(usuario, newPassword);
    }

    private boolean changePassword(Usuario usuario, String newPassword) { 
        try { 
            if (usuario != null) {    
                usuario.setPassword(bCryptPasswordEncoder.encode(newPassword));  
                usuarioRepository.save(usuario);
                return true; 
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void savePasswordResetToken(Usuario usuario, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, usuario);  
        PasswordResetToken existingToken = passwordResetTokenRepository.getExistTokenByUser(usuario.getId());
        if (existingToken != null) {
            passwordResetTokenRepository.delete(existingToken);
        }
        
        passwordResetTokenRepository.save(passwordResetToken);
    } 

}
