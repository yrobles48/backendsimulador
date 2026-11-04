package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudHistory;

public interface LCRSolicitudHistoryRepository extends JpaRepository<LCRSolicitudHistory, Long> {

    @Query("SELECT h FROM LCRSolicitudHistory h WHERE h.updatedBy IS NULL and h.lctSolicitud.id = :lctSolicitudId" +
            " and h.id =(SELECT MAX(h2.id) FROM LCRSolicitudHistory h2 WHERE h2.updatedBy IS NULL and h2.lctSolicitud.id = :lctSolicitudId)")
    LCRSolicitudHistory findTopByUpdatedByIsNull(Long lctSolicitudId);

    @Query("SELECT h FROM LCRSolicitudHistory h WHERE h.updatedBy IS NULL and h.lctSolicitud.id = :lctSolicitudId AND h.id = (SELECT MAX(h2.id) FROM LCRSolicitudHistory h2 WHERE h2.updatedBy IS NULL and h2.lctSolicitud.id = :lctSolicitudId)")
    LCRSolicitudHistory findFirstByOrderByIdDesc(Long lctSolicitudId);

    @Query("SELECT h FROM LCRSolicitudHistory h WHERE h.lctSolicitud.numOperacion = :numOperacion ORDER BY h.createdDate DESC")
    List<LCRSolicitudHistory> findByNumOperacion(Long numOperacion);

    @Query(value = "SELECT lsh.*\r\n" + //
            "FROM lcr_solicitud_history lsh\r\n" + //
            "inner join lcr_solicitud ls on ls.id = lsh.lct_solicitud_id\r\n" + //
            "WHERE lsh.lct_solicitud_etapa_id = 3\r\n" + //
            "  AND ls.num_operacion = :numOperacion\r\n" + //
            "  AND lsh.id = (\r\n" + //
            "      SELECT MAX(sub.id)\r\n" + //
            "      FROM lcr_solicitud_history sub\r\n" + //
            "      WHERE sub.lct_solicitud_etapa_id = 3\r\n" + //
            "        AND sub.lct_solicitud_id = lsh.lct_solicitud_id\r\n" + //
            "  )", nativeQuery = true)
    LCRSolicitudHistory findByNumOperacionAndUpdatedByIsNull(@Param("numOperacion") Long numOperacion);

}
