package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTFiles;

@Repository
public interface LCTFilesRepository extends JpaRepository<LCTFiles, Long> {

    @Query("SELECT l FROM LCTFiles l WHERE l.liquidacionCostosTransaccion.id = :liquidacionCostosTransaccionId")
    List<LCTFiles> findByLiquidacionCostosTransaccionId(@Param("liquidacionCostosTransaccionId") Long liquidacionCostosTransaccionId);

    @Query("SELECT l FROM LCTFiles l WHERE l.liquidacionCostosTransaccion.id = :liquidacionCostosTransaccionId AND l.tag = :tag")
    LCTFiles findByLiquidacionCostosTransaccionIdAndTag(@Param("liquidacionCostosTransaccionId") Long liquidacionCostosTransaccionId, @Param("tag") String tag);
}
