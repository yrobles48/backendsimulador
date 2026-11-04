package com.prestaclub.prestaclub_backend.persistence.nuevo.repository; 
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRMontoEstadosDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;

public interface LiquidacionCostosTransaccionRepository extends JpaRepository<LiquidacionCostosTransaccion, Long> {
    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion AND l.esDesembolso = true ORDER BY l.id DESC LIMIT 1")
    LiquidacionCostosTransaccion findLastByNumOperacion(@Param("numOperacion") Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.id = :liquidacionCostosTransaccionId")
    LiquidacionCostosTransaccion findLastById(@Param("liquidacionCostosTransaccionId") Integer liquidacionCostosTransaccionId);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion ORDER BY l.esFinal DESC, l.esDesembolso DESC, l.id DESC")
    Page<LiquidacionCostosTransaccion> getListaLiquidacionCostosTransaccion(@Param("numOperacion") Integer numOperacion, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion AND l.esFinal = true")
    boolean existsFinal(@Param("numOperacion") Long numOperacion);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion AND l.esDesembolso = true")
    boolean existsDesembolso(@Param("numOperacion") Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion AND l.esFinal = true ORDER BY l.id LIMIT 1")
    LiquidacionCostosTransaccion findEsFinalByNumOperacion(@Param("numOperacion") Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion AND l.esDesembolso = true ORDER BY l.id LIMIT 1")
    LiquidacionCostosTransaccion findEsDesembolsoByNumOperacion(@Param("numOperacion") Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.esFinal = true")
    List<LiquidacionCostosTransaccion> listaFinalLiquidaciones();

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.esDesembolso = true ORDER BY l.numOperacion DESC")
    List<LiquidacionCostosTransaccion> listaDesembolsoLiquidaciones();

    @Query("SELECT l FROM LiquidacionCostosTransaccion l " +
        "left join LCRSolicitud ls on ls.numOperacion = l.numOperacion " +
        "left join LCRSolicitudEtapaSeguimiento lses on lses.lcrSolicitud.id = ls.id " +
        "left JOIN LCRSolicitudEtapa lse ON lse.id = lses.lcrSolicitudEtapa.id " +
        "WHERE l.esDesembolso = true and ( lse.slug is null or lse.slug = :slug  )" +
        "AND (:statusList IS NULL or  lses.status in (:statusList)) "+
        "AND (:filterText IS NULL OR :filterText = '' OR CAST(l.numOperacion AS string) LIKE LOWER(CONCAT('%', :filterText, '%')))"+ 
        " ORDER BY l.updatedDate DESC")
    // Page<LiquidacionCostosTransaccion> listaDesembolsoLiquidacionesByCust(@Param("filterText") String filterText, @Param("statusList") List<Long> statusList, Pageable pageable);
    Page<LiquidacionCostosTransaccion> listaDesembolsoLiquidacionesByCust(@Param("filterText") String filterText, @Param("slug") String slug,@Param("statusList") List<Long> statusList , Pageable pageable);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l " +
        "WHERE l.esFinal = true " +
        "AND (:filterText IS NULL OR :filterText = '' OR CAST(l.numOperacion AS string) LIKE LOWER(CONCAT('%', :filterText, '%')))"+
        " ORDER BY l.updatedDate DESC")
    Page<LiquidacionCostosTransaccion> listaFinalLiquidacionesByCust(@Param("filterText") String filterText, Pageable pageable);


    @Query("SELECT count(1) FROM LiquidacionCostosTransaccion l WHERE l.esFinal = true")
    Integer countLctFinal();


//     select count(1) from liquidacion_costos_transacciones lct 
// left join lcr_solicitud lcrs on lcrs.num_operacion = lct.num_operacion
// where lct.es_desembolso = true and lcrs.id is not null;

    @Query(value = "select count(1) from liquidacion_costos_transacciones lct \r\n" + //
                "left join lcr_solicitud lcrs on lcrs.num_operacion = lct.num_operacion\r\n" + //
                "where lct.es_desembolso = true and lcrs.id is null", nativeQuery = true)
    Integer countLctDesembolso();

    @Query("SELECT l.esDolares FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion and l.esDesembolso = true")
    Boolean getEsDolaresbyTchn(@Param("numOperacion") Long numOperacion);


    @Query("SELECT l.totalCostosTransaccion FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion and l.esDesembolso = true")
    BigDecimal getTotalCostosTransaccionByNumOperacion(@Param("numOperacion") Long numOperacion);

    @Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.numOperacion = :numOperacion and l.esDesembolso = true")
    LiquidacionCostosTransaccion getTotalCostosTransaccionBySolNumOperacion(@Param("numOperacion") Long numOperacion);


//     select  
// 	SUM(CASE WHEN lses.status = 0 THEN 1 ELSE 0 END) AS pendiente,
//     SUM(CASE WHEN lses.status = 1 THEN 1 ELSE 0 END) AS aprobado,
//     SUM(CASE WHEN lses.status = 2 THEN 1 ELSE 0 END) AS observado
// 	from liquidacion_costos_transacciones lct 
// inner join lcr_solicitud ls on ls.num_operacion = lct.num_operacion 
// inner join lcr_solicitud_etapa_seguimiento lses on lses.lcr_solicitud_id = ls.id
// inner join lcr_solicitud_etapa lse on lse.id = lses.lcr_solicitud_etapa_id 
// where lct.es_desembolso = true and lse.slug = 'negocios' 
// ;
    @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRMontoEstadosDTO(" +
        "SUM(CASE WHEN lses.status = 0 THEN 1 ELSE 0 END) , " +
        "SUM(CASE WHEN lses.status = 1 THEN 1 ELSE 0 END)  , " +
        "SUM(CASE WHEN lses.status = 2 THEN 1 ELSE 0 END)) " +
        "FROM LiquidacionCostosTransaccion lct " +
        "INNER JOIN LCRSolicitud ls ON ls.numOperacion = lct.numOperacion " +
        "INNER JOIN LCRSolicitudEtapaSeguimiento lses ON lses.lcrSolicitud.id = ls.id " +
        "INNER JOIN LCRSolicitudEtapa lse ON lse.id = lses.lcrSolicitudEtapa.id " +
        "WHERE lct.esDesembolso = true AND lse.slug = :slug")
    LCRMontoEstadosDTO getMontoEstados(@Param("slug") String slug);

@Query("SELECT l FROM LiquidacionCostosTransaccion l WHERE l.esDesembolso = true and date(l.fechaAdeudado) between date(:startDate) and date(:endDate) ORDER BY l.updatedDate DESC")
    List<LiquidacionCostosTransaccion> listaDesembolsoLiquidacionesByRangeUpdateDate(@Param("startDate") String startDate, @Param("endDate") String endDate);    
}
