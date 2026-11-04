package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;

public interface LCRServiciosRepository extends JpaRepository<LCRServicios, Long> {
    LCRServicios findBySlug(String slug);
    List<LCRServicios> findAll();
    LCRServicios findBySlugAndLcrConceptosServiciosId(String slug, Long id);
}
