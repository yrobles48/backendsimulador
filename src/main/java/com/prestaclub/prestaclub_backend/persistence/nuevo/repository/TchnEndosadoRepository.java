package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TchnEndosado;

public interface TchnEndosadoRepository extends JpaRepository<TchnEndosado, Integer> {
    TchnEndosado findByCodigoOperacion(Long codigoOperacion);
    TchnEndosado findByCodigoSunarp(String codigoSunarp);
}
