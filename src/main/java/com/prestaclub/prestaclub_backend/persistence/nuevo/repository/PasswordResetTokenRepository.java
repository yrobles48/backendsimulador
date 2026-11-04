package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> { 
    PasswordResetToken findByToken(String token); 
    
    @Query("SELECT p FROM PasswordResetToken p WHERE p.usuario.id = :id_user")
    PasswordResetToken getExistTokenByUser(@Param("id_user") Long id_user);
}
