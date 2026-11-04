package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoEstados;

 @Repository
public interface LCRReembolsoEstadosRepository extends JpaRepository<LCRReembolsoEstados, Long> {
    LCRReembolsoEstados findBySlug(String slug); 
}
