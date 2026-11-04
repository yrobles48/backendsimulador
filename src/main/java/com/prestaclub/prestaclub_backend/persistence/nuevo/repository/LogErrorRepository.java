package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LogError;

public interface LogErrorRepository extends JpaRepository<LogError, Long> {
    
}
