package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRConceptosServicios;

public interface LCRConceptosServiciosRepository extends JpaRepository<LCRConceptosServicios, Long> {
    LCRConceptosServicios findBySlug(String slug);
}
