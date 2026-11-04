package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

}
