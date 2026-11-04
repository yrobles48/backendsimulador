package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolso;

import java.util.List;

public interface LCRReembolsoRepository extends JpaRepository<LCRReembolso, Long> {

        @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO(ls,l, rm.faplicacion,rm.fdevolucion, rm.envio1, ls.montoSimulado, ls.montoReal,ls.montoSimulado - ls.montoReal, rm.fechaenvio1, rm.fechaenvio2) "
                +
                "FROM LCRReembolso l " +
                "JOIN l.lcrReembolsoSubEstados sub " +
                "JOIN sub.lcrReembolsoEstados est " +
                "JOIN LCRSolicitud ls ON ls.numOperacion = l.numOperacion " +
                "left JOIN RptMensaje rm ON l.numOperacion = rm.num_operacion and rm.activo=true " +
                "WHERE   (:filterText IS NULL OR :filterText = '' OR CAST(l.numOperacion AS string) LIKE CONCAT('%', :filterText, '%')) " +
                " and (rm.esconta=false or rm.esconta is null) "+
                "AND est.id = :status " +
                "ORDER BY l.id DESC")
        Page<LCRReembolsoDTO> getListaLcrReembolsoByFilter(
                @Param("filterText") String filterText,
                @Param("status") Long status,
                Pageable pageable);


        @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO(ls,l, rm.faplicacion,rm.fdevolucion, rm.envio1, ls.montoSimulado, ls.montoReal,ls.montoSimulado - ls.montoReal, rm.fechaenvio1, rm.fechaenvio2) "
                +
                "FROM LCRReembolso l " +
                "JOIN l.lcrReembolsoSubEstados sub " +
                "JOIN sub.lcrReembolsoEstados est " +
                "JOIN LCRSolicitud ls ON ls.numOperacion = l.numOperacion " +
                "left JOIN RptMensaje rm ON l.numOperacion = rm.num_operacion and rm.activo=true " +
                "WHERE   (:filterText IS NULL OR :filterText = '' OR CAST(l.numOperacion AS string) LIKE CONCAT('%', :filterText, '%')) "
                +
                "AND est.id = :status AND rm.fechaenvio2 IS NULL " +
                "ORDER BY l.id DESC")
        Page<LCRReembolsoDTO> getListaLcrReembolsoByFilterContabilidad(
                @Param("filterText") String filterText,
                @Param("status") Long status,
                Pageable pageable);

        @Query(value = "SELECT l.id, l.num_operacion, ls.es_dolares,rm.envio1, " +
                " case when lct.es_dolares=false then lct.costos_transaccion else round(lct.costos_transaccion * (tipo_cambio_peritaje-0.2),2)  end + "+
                " case when lct.es_dolares=false then pagado_cliente else pagado_cliente * (tipo_cambio_peritaje-0.2) end "+
                " AS montoSimulado, "+
                " (select sum(case when es_dolares=true then round(monto * tipo_cambio,2) else monto end ) "+
                " from public.liquidacion_costos_reales where num_operacion=l.num_operacion "+
                " and deleted_at is null) AS montoReal, "+
                "	 case when lct.es_dolares=false then lct.costos_transaccion else round(lct.costos_transaccion * (tipo_cambio_peritaje-0.2),2)  end   +  case when lct.es_dolares=false then pagado_cliente else pagado_cliente * tipo_cambio_peritaje end "+
                "     - ( select sum(case when es_dolares=true then round(monto * tipo_cambio,2) else monto end ) "+
                " from public.liquidacion_costos_reales where num_operacion=l.num_operacion "+
                " and deleted_at is null) as saldo, false AS flag FROM lcr_reembolso l " +
                " JOIN lcr_reembolso_sub_estados sub " +
                " ON l.lcr_reembolso_sub_estados_id = sub.id "+
                " JOIN public.liquidacion_costos_transacciones lct "+
                " ON lct.num_operacion=l.num_operacion and lct.es_desembolso=true "+
                " JOIN lcr_reembolso_estados est "+
                " ON sub.lcr_reembolso_estados_id = est.id "+
                " JOIN lcr_solicitud ls "+
                " ON ls.num_operacion = l.num_operacion "+
                " JOIN rpt_mensaje rm ON l.num_operacion = rm.num_operacion "+
                " AND rm.activo = true " +
                "  AND l.lcr_reembolso_sub_estados_id = 2 " +
                "                    AND rm.fechaenvio2 + interval '15 day' < current_date and rm.esconta=true " +
                "ORDER BY l.id DESC", nativeQuery = true)
        List<Object[]> getListaLcrContabilidad();

        @Query(value = "SELECT COUNT(*) " +
                "FROM lcr_reembolso l " +
                "JOIN lcr_reembolso_sub_estados sub ON l.lcr_reembolso_sub_estados_id = sub.id " +
                "JOIN lcr_reembolso_estados est ON sub.lcr_reembolso_estados_id = est.id " +
                "JOIN lcr_solicitud ls ON ls.num_operacion = l.num_operacion " +
                "JOIN rpt_mensaje rm ON l.num_operacion = rm.num_operacion " +
                "    AND rm.activo = true  and rm.esconta=true" +
                "    AND rm.fechaenvio2 + interval '15 day' < current_date " +
                "WHERE est.id = 2", nativeQuery = true)
        Integer getTotalContabilidad();

}
