package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;

@Repository
public interface LCTCronogramaRepository extends JpaRepository<LCTCronograma, Long> {
    @Query("SELECT l FROM LCTCronograma l WHERE l.liquidacionCostosTransaccionId.id = :liquidacionCostosTransaccionId")
    List<LCTCronograma> findByLiquidacionCostosTransaccionId(Long liquidacionCostosTransaccionId);
}
