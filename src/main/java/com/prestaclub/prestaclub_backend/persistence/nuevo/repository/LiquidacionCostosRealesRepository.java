package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;

public interface LiquidacionCostosRealesRepository extends JpaRepository<LiquidacionCostosReales, Long> {
    @Query("SELECT l FROM LiquidacionCostosReales l WHERE l.lcrServicios.id is not null and l.deletedAt is null and l.numOperacion = :numOperacion  ORDER BY l.createdAt DESC, l.updatedAt DESC")
    List<LiquidacionCostosReales> findByNumOperacion(Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosReales l WHERE l.deletedAt is null and l.numOperacion = :numOperacion and l.lcrServicios.id = 4 ORDER BY l.createdAt DESC, l.updatedAt DESC")
    List<LiquidacionCostosReales> findByNumOperacionAndAsesoriaPrestaclub(Long numOperacion);

    @Query("SELECT sum(l.monto) FROM LiquidacionCostosReales l WHERE l.deletedAt is null and l.numOperacion = :numOperacion ")
    BigDecimal getTotalRegistroPagosByNumOperacion(@Param("numOperacion") Long numOperacion);

    @Query("SELECT sum(l.monto) FROM LiquidacionCostosReales l WHERE l.deletedAt is null and l.numOperacion = :numOperacion and l.lcrServicios.id = 3")
    BigDecimal getTotalPeritajeByNumOperacion(Long numOperacion);
}
