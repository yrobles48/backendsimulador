package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.controller.LiquidacionCostosRealesController;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;

import jakarta.transaction.Transactional;

public interface LCRSolicitudEtapaSeguimientoRepository extends JpaRepository<LCRSolicitudEtapaSeguimiento, Long> {

        @Query("SELECT e FROM LCRSolicitudEtapaSeguimiento e WHERE e.lcrSolicitud.numOperacion = :numOperacion order by e.lcrSolicitudEtapa.id asc")
        List<LCRSolicitudEtapaSeguimiento> findByLcrSolicitudNumOperacion(@Param("numOperacion") Long numOperacion);

        @Query("SELECT e.status FROM LCRSolicitudEtapaSeguimiento e WHERE e.lcrSolicitud.numOperacion = :numOperacion and e.actual = true")
        Integer getStatusByNumOperacion(@Param("numOperacion") Long numOperacion);

        @Query("SELECT e FROM LCRSolicitudEtapaSeguimiento e WHERE e.lcrSolicitud.id = :lcrSolicitudId and e.actual = true")
        LCRSolicitudEtapaSeguimiento findByLcrSolicitudIdAndActual(@Param("lcrSolicitudId") Long lcrSolicitudId);

        @Query("SELECT e FROM LCRSolicitudEtapaSeguimiento e WHERE e.lcrSolicitud.numOperacion = :numOperacion and e.actual = true")
        LCRSolicitudEtapaSeguimiento findByLcrSolicitudNumOperacionAndActual(@Param("numOperacion") Long numOperacion);

        @Query("SELECT e FROM LCRSolicitudEtapaSeguimiento e WHERE e.lcrSolicitud.id = :lcrSolicitudId and e.lcrSolicitudEtapa.slug = :slug")
        LCRSolicitudEtapaSeguimiento findByLcrSolicitudIdAndLcrSolicitudEtapaSlug(
                        @Param("lcrSolicitudId") Long lcrSolicitudId, @Param("slug") String slug);

        @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse(" +
                        "e.lcrSolicitud.id, e.lcrSolicitud.numOperacion, e.status, l.montoSimulado, l.montoReal, l.createdAt, l.esDolares ) from LCRSolicitudEtapaSeguimiento e "
                        +
                        " inner join LCRSolicitud l on l.id = e.lcrSolicitud.id" +
                        " where (:filterText IS NULL OR :filterText = '' OR  CAST(e.lcrSolicitud.numOperacion AS string) LIKE CONCAT('%', :filterText, '%')) and e.status = :status and e.actual = true and e.lcrSolicitudEtapa.slug = 'contabilidad' ")
        Page<LcrSolicitudResponse> getLcrSolicitudByStatus(@Param("status") Integer status,
                        @Param("filterText") String filterText, Pageable pageable);

        @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse(" +
                        "e.lcrSolicitud.id, e.lcrSolicitud.numOperacion, e.status,  (lct.criBloqueoPeritaje + lct.pagosDescontadosCheque + lct.costosAdicionales) as montoSimulado, l.montoReal , l.createdAt, l.esDolares ) from LCRSolicitudEtapaSeguimiento e "
                        +
                        " inner join LCRSolicitud l on l.id = e.lcrSolicitud.id join TchnEndosado t on t.codigoOperacion = l.numOperacion inner join LiquidacionCostosTransaccion lct on lct.numOperacion = l.numOperacion" +
                        " where lct.esDesembolso = true and ( t.fechaEndoso between :fechaini and :fechafin) and (:status IS NULL OR :status = e.status) and e.actual = true and e.lcrSolicitudEtapa.slug = 'contabilidad' ")
        List<LcrSolicitudResponse> getLcrSolicitudByStatusByDates(@Param("fechaini") LocalDateTime fechaini,
                        @Param("fechafin") LocalDateTime fechafin, @Param("status") Integer status);

        @Query("SELECT count(1) FROM LCRSolicitudEtapaSeguimiento e WHERE e.status = 0 and e.actual = true and e.lcrSolicitudEtapa.slug = :slug")
        Integer countPendienteBySlug(String slug);

        @Query("SELECT tipoCambioPeritaje FROM LiquidacionCostosTransaccion tc WHERE tc.numOperacion = :numOperacion order by tc.id desc limit 1")
        BigDecimal getTCSol(Long numOperacion);

        @Query("SELECT tc FROM LiquidacionCostosTransaccion tc WHERE tc.esDesembolso = true and tc.numOperacion = :numOperacion order by tc.id desc limit 1")
        LiquidacionCostosTransaccion getLiquidacionCostosTransaccion(Long numOperacion);

        @Query("SELECT tc FROM LiquidacionCostosReales tc WHERE tc.deletedAt IS NULL and tc.numOperacion = :numOperacion ")
        List<LiquidacionCostosReales> getLiquidacionCostosReales(Long numOperacion);

        @Modifying
        @Transactional
        @Query(value = "update lcr_solicitud_etapa_seguimiento set status=1 where actual= true and lcr_solicitud_id =:id", nativeQuery = true)
        void updateEstadoAprobado(@Param("id") Long id);

}
