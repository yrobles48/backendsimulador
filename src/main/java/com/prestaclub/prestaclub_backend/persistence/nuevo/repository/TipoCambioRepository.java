package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TipoCambio;

@Repository
public interface TipoCambioRepository extends JpaRepository<TipoCambio, Long> {
    //OBTENER ULTIMO TIPO DE CAMBIO EN BASE A LA ULTIMO FECHA REGISTRADA 
    @Query("SELECT t FROM TipoCambio t ORDER BY t.fecha desc,t.id DESC LIMIT 1")
    TipoCambio findAllByOrderByFechaDesc();
    //query que traiga los ultimos 5 tipos de cambio ordenado por fecha de creacio
    @Query("SELECT t FROM TipoCambio t ORDER BY t.fecha desc,t.id DESC LIMIT 5")
    List<TipoCambio> findLastTipoCambio();
}
