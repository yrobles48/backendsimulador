package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudFiles;
 
public interface LCRSolicitudFilesRepository extends JpaRepository<LCRSolicitudFiles, Long> {
    @Query("SELECT f FROM LCRSolicitudFiles f WHERE f.lcrSolicitud.id = :lcrSolicitudId")
    List<LCRSolicitudFiles> findByNumOperacion(Long lcrSolicitudId);
}
