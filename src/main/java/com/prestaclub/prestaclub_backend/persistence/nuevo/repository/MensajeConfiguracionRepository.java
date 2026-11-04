package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.MensajeConfiguracion;

public interface MensajeConfiguracionRepository extends JpaRepository<MensajeConfiguracion, Long> {
    MensajeConfiguracion findBySlug(String slug);
}
