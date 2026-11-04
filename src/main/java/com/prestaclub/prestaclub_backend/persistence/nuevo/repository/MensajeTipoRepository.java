package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.MensajeTipo;

public interface MensajeTipoRepository extends JpaRepository<MensajeTipo, Long> {
    MensajeTipo findBySlug(String slug);
}
