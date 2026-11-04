package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapa;

public interface LCRSolicitudEtapaRepository extends JpaRepository<LCRSolicitudEtapa, Long> {
    LCRSolicitudEtapa findBySlug(String slug);
}
