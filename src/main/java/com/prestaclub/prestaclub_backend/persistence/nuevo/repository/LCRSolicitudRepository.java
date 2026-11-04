package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.FlujoTchnReporteProjection;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.RptMensaje;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;

public interface LCRSolicitudRepository extends JpaRepository<LCRSolicitud, Long> {
    // @Query("SELECT l FROM LCRSolicitud l WHERE l.status = :status order by
    // l.updatedAt desc")
    // List<LCRSolicitud> findByNumOperacionAndStatus( Integer status);

    @Query("SELECT l FROM LCRSolicitud l WHERE   l.numOperacion = :numOperacion ")
    LCRSolicitud findByNumOperacion(Long numOperacion);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LCRSolicitud l WHERE   l.numOperacion = :numOperacion ")
    Boolean getExistSolicitudByNumOperacion(Long numOperacion);

    // @Query("SELECT l.status FROM LCRSolicitud l WHERE l.numOperacion =
    // :numOperacion ")
    // Integer getStatusByNumOperacion(Long numOperacion);

    // @Query("SELECT COUNT(l) FROM LCRSolicitud l WHERE l.status = 1")
    // Integer countLcrSolicitudPendiente();

    // //select * from lcr_solicitud where status = 3 and date(updated_at) =
    // date('2025-02-27');
    // @Query(value = "select * from lcr_solicitud where status = 3 and
    // date(updated_at) between date(:startDate) and date(:endDate)", nativeQuery =
    // true)
    // List<LCRSolicitud> findByRangeUpdateDate(@Param("startDate") String
    // startDate, @Param("endDate") String endDate);

    @Query(value = """
            SELECT
              te.codigooperacion AS "TCHN",
              te.fechaendoso AS "FECHA_ENDOSO",
              lct.costos_transaccion AS "MONTO_SIMULADO",
               lct.pagado_cliente as "PAGADO_CLIENTE",
              (SELECT COALESCE(SUM(x.monto), 0) FROM liquidacion_costos_reales x
               WHERE x.deleted_by IS NULL AND x.num_operacion = lct.num_operacion) AS "MONTO_REAL",
               lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - pagado_cliente  -     
              (SELECT COALESCE(SUM(x.monto), 0) FROM liquidacion_costos_reales x
               WHERE x.deleted_by IS NULL AND x.num_operacion = lct.num_operacion) AS "MONTO_SALDO_TOTAL",
              seg.status AS "ESTADO_POR_AREA",
              seg.nombre AS "USUARIO_AREA",
              CASE WHEN lcs.slug IN ('peritaje','costo-de-inspeccion') THEN 'NEGOCIOS' ELSE 'LEGAL' END AS "AREA",
              CONCAT(actual.descripcion,' (',actual.status,')') AS "ESTADO_ACTUAL",
              actual.nombre AS "USUARIO_ACTUAL",
              actual.updated_at AS "FECHA_ACTUALIZADA",
              actual.mensaje AS "OBSERVACIONES",
              lcs.descripcion AS "CONCEPTO",
              CASE
              	WHEN lcs.slug = 'asesoria-prestaclub' THEN lct.asesoria_prestaclub
              	WHEN lcs.slug = 'bloqueo-registral' THEN lct.bloqueo_registral
              	WHEN lcs.slug = 'cheque-de-gerencia-extra' THEN lct.cheque_gerencia_extra
              	WHEN lcs.slug = 'costo-de-inspeccion' THEN lct.costo_inspeccion
              	WHEN lcs.slug = 'cri-y-copia-literal' THEN lct.certificado_registral_inmobiliario
              	WHEN lcs.slug = 'derechos-notariales' THEN lct.derechos_notariales
              	WHEN lcs.slug = 'derechos-registrales' THEN lct.derechos_registrales
              	WHEN lcs.slug = 'otorgamiento-de-poder' THEN lct.otorgamiento_poder
              	WHEN lcs.slug = 'peritaje' THEN lct.peritaje
              	WHEN lcs.slug = 'registrales-de-levantamiento' THEN lct.registrales_levantamiento
              	WHEN lcs.slug = 'toma-de-firmas-con-lector-biometrico' THEN lct.toma_firmas_biometrico
              END AS "GASTO_SIMULADO",
            coalesce(monto.monto, 0) as "GASTO_REAL",
            round(
                case when lct.es_dolares= false then  lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales -  lct.pagado_cliente else round((lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - lct.pagado_cliente) * (lct.tipo_cambio_peritaje-0.2),2) end
                - sum(coalesce(monto.monto, 0)) over (
                    partition by te.codigooperacion
                    order by lcs.descripcion
                    rows between unbounded preceding and current row
                ),
                2
            ) as "SALDO"
            FROM
              tchn_endosado te
            INNER JOIN liquidacion_costos_transacciones lct ON lct.num_operacion = te.codigooperacion
            LEFT JOIN lcr_servicios ls ON true
            LEFT JOIN lcr_conceptos_servicios lcs ON lcs.id = ls.lcr_conceptos_servicios_id
            LEFT JOIN liquidacion_costos_reales lcr ON lcr.num_operacion = lct.num_operacion
                AND lcr.lcr_servicios_id = ls.id AND lcr.deleted_by IS NULL
            LEFT JOIN lcr_solicitud lso ON lso.num_operacion = te.codigooperacion
            LEFT JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = lso.id
            LEFT JOIN(
                select lcr.num_operacion,ls.lcr_conceptos_servicios_id, sum(lcr.monto) as monto from liquidacion_costos_reales lcr
                inner join lcr_servicios ls on ls.id = lcr.lcr_servicios_id
                inner join tchn_endosado te on te.codigooperacion = lcr.num_operacion
                where lcr.deleted_by is null
                group by 1,2
            ) monto on monto.num_operacion = te.codigooperacion and monto.lcr_conceptos_servicios_id = lcs.id
            LEFT JOIN (
            	SELECT
            		lses.lcr_solicitud_id,
            		ls.num_operacion,
            		lse.descripcion,
            		CASE
            			WHEN lses.status = 0 THEN 'PENDIENTE'
            			WHEN lses.status = 1 THEN 'APROBADO'
            			ELSE 'OBSERVADO'
            		END AS status,
            		CONCAT(e.nombre, ' ',e.apellido_paterno) AS nombre,
            		lses.updated_at ,
            		lse.slug
            	FROM lcr_solicitud ls
            	INNER JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = ls.id
            	INNER JOIN lcr_solicitud_etapa lse ON lse.id = lses.lcr_solicitud_etapa_id
            	INNER JOIN usuarios u ON u.id = lses.updated_by
            	INNER JOIN empleados e ON e.id = u.id_empleado
            ) seg ON seg.num_operacion = te.codigooperacion
                 AND seg.slug = CASE WHEN lcs.slug IN ('peritaje','costo-de-inspeccion') THEN 'negocios' ELSE 'legal' END
            LEFT JOIN (
            	SELECT
            		ls.num_operacion,
            		lse.descripcion,
            		CASE
            			WHEN lses.status = 0 THEN 'PENDIENTE'
            			WHEN lses.status = 1 THEN 'APROBADO'
            			ELSE 'OBSERVADO'
            		END AS status,
            		CONCAT(e.nombre, ' ',e.apellido_paterno) AS nombre,
            		lses.updated_at,
            		lsh.mensaje
            	FROM lcr_solicitud ls
            	INNER JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = ls.id
            	INNER JOIN lcr_solicitud_etapa lse ON lse.id = lses.lcr_solicitud_etapa_id
            	INNER JOIN usuarios u ON u.id = lses.updated_by
            	INNER JOIN empleados e ON e.id = u.id_empleado
            	LEFT JOIN lcr_solicitud_history lsh ON lsh.lct_solicitud_id = ls.id AND lsh.updated_at IS NULL
            	WHERE lses.actual = true
            ) actual ON actual.num_operacion = te.codigooperacion
            WHERE lct.es_desembolso = true  and  ( :numOperacion IS NULL or :numOperacion = ''  OR cast(te.codigooperacion as TEXT) = :numOperacion)
            GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16
            ORDER BY te.codigooperacion, lcs.descripcion
            """, nativeQuery = true)
    List<FlujoTchnReporteProjection> obtenerDetalleCostos(@Param("numOperacion") String numOperacion);

    @Query(value = "select " + //
            "usr.username as username,'Contabilidad' as area,(l2_0.monto_simulado-l2_0.monto_real) as devolucion, " + //
            "men.numoperacion as numoperacion ,men.copropietario as copropietario , men.fechaenvio1 as fechaenvio1, " + //
            "'Correo' as tipoenvio1, 'Sin Respuesta' as tiporespuesta1 ,men.fechaenvio2 as fechaenvio2,'Correo' as tipoenvio2 "
            + //
            ",'Sin Respuesta' as tiporespuesta2 ,men.fechaenvio1 as fecharespuesta , 'Reembolsado' as estadofinal ,men.dmodify_by as modificadopor, "
            + //
            "men.fechaenvio1 as fechaendoso " + //
            "from lcr_reembolso l1_0 join lcr_reembolso_sub_estados lrse1_0 on lrse1_0.id=l1_0.lcr_reembolso_sub_estados_id "
            + //
            "join lcr_reembolso_estados lre1_0 on lre1_0.id=lrse1_0.lcr_reembolso_estados_id " + //
            "join lcr_solicitud l2_0 on l2_0.num_operacion=l1_0.num_operacion " + //
            "join rpt_mensaje men on men.num_operacion = l1_0.num_operacion " + //
            "join usuarios usr on usr.id = men.created_by " + //
            "left join rpt_mensaje rm1_0 on l1_0.num_operacion=rm1_0.num_operacion and rm1_0.activo=true " + //
            "where " + //
            " (:tchn = '' or :tchn = cast(men.numoperacion as TEXT)) and CAST(men.dcreate_by AS date) between CAST(:startdate AS date) and CAST(:enddate AS date) and "
            + //
            " ( :reembolsados = 'false' or  lrse1_0.lcr_reembolso_estados_id =4 ) and  ( :aplicados = 'false' or  lrse1_0.lcr_reembolso_estados_id =6 ) "
            + //
            "order by l1_0.id desc ", nativeQuery = true)
    List<RptMensaje> obtenerRptMensaje(
            @Param("startdate") String startdate,
            @Param("enddate") String enddate,
            @Param("tchn") String tchn,
            @Param("reembolsados") String reembolsados,
            @Param("aplicados") String aplicados);



    @Query(value = """
    select lre.id,
    lr.num_operacion  as numoperacion ,
    lct.nombre_solicitante_cheque as copropietario,
    round(
             case when lct.es_dolares is true
            then ((lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + lct.costos_adicionales ) * (lct.tipo_cambio_peritaje - 0.2))
            else
            (lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + lct.costos_adicionales  )
    end  -
                    (
    SELECT sum (case when es_dolares is true  then (monto *tipo_cambio) else monto  end) as total_monto
    FROM public.liquidacion_costos_reales
    where num_operacion =lr.num_operacion and deleted_at is null
            )
            ,2) as devolucion ,
    te.fechaendoso,
    rm.fechaenvio1,
            case when rm.fechaenvio1 is not null then 'Correo' end as tipoenvio1,
            case
    when (rm.fechaenvio1 is null and rm.fechaenvio2 is null) then 'Pendiente'
    when (rm.fechaenvio2 is null and rm.fechaenvio1 is not null) then rm.envio1
    when (rm.fechaenvio2 is not null and rm.fechaenvio1 is not null) then 'Sin Respuesta'
    end   as  tiporespuesta1,
    rm.fechaenvio2,
            case when rm.fechaenvio2 is not null then 'Correo' end as tipoenvio2,
            case
    when (rm.fechaenvio2 is not null and rm.fechaenvio1 is not null and rm.banco is null) then 'Sin Respuesta'
    when lre.id =4 and rm.fechaenvio2 is not null then 'Con cuenta' when lre.id =3 and rm.fechaenvio2 is not null then 'Con cuenta'
    end   as  tiporespuesta2,
    now()::date as fecharespuesta,
            case when lre.id=2 then 'En evaluación' when lre.id=6 then 'Aplicado contablemente' when lre.id=4 then 'Reembolsado' 
             when lre.id=5 then 'Por aplicar' when lre.id=3 then 'Por reembolsar' when lre.id=2 then 'Excepciones' end as estadofinal  ,
    dmodify_by as ultimamodificacion ,
    usr.username as username,
            case when lre.id=2 then 'Administración'  when lre.id=3 then 'Administración'  
            when lre.id=4 then 'Administración' else 'Contabilidad' end as area
    from lcr_reembolso lr
    inner join lcr_reembolso_sub_estados lrse on lr.lcr_reembolso_sub_estados_id =lrse.id
    inner join lcr_reembolso_estados lre on lre.id = lrse.lcr_reembolso_estados_id
    inner join lcr_solicitud ls on lr.num_operacion =ls.num_operacion
    inner join tchn_endosado te on te.codigooperacion =lr.num_operacion
    inner join liquidacion_costos_transacciones lct on lct.num_operacion=lr.num_operacion
    left join rpt_mensaje rm on rm.num_operacion = lr.num_operacion
    left join usuarios usr on usr.id = rm.created_by
    where lre.id <>1 and lct.es_desembolso=true and
            ( :estado = 0  OR   (:estado = 2 AND lre.id IN (2, 3, 5))    OR 
                (:estado <> 2 AND lre.id = :estado)) 
            and (:tchn = '' or :tchn = cast(lr.num_operacion as TEXT))   
            and CAST(te.fechaendoso AS date) between CAST(:startdate AS date) and CAST(:enddate AS date)
            """,            nativeQuery = true)
    List<RptMensaje> obtenerRptMensaje(
            @Param("startdate") String startdate,
            @Param("enddate") String enddate,
            @Param("tchn") String tchn,
            @Param("estado") Integer estado);

    @Query(value = """         
                 SELECT   te.codigooperacion AS "TCHN",lct.es_dolares,
            					lct.costos_transaccion,lct.pagado_cliente ,lct.tipo_cambio_peritaje,
            					   lct.pagado_cliente  ,
            					  te.fechaendoso AS "FECHA_ENDOSO",
								  lcr.lcr_servicios_id,
            						case when lct.es_dolares= false then  lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - pagado_cliente   else round(( lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - pagado_cliente) * (lct.tipo_cambio_peritaje-0.2),2) end
            						AS "MONTO_SIMULADO",
            						 (SELECT COALESCE(SUM(CASE WHEN x.es_dolares=false then x.monto else  round(x.monto*x.tipo_cambio,2) end ), 0) FROM liquidacion_costos_reales x
            						  WHERE x.deleted_by IS NULL AND x.num_operacion = lct.num_operacion )  -
            						  case when lct.es_dolares= false then  lct.pagado_cliente  else round(lct.pagado_cliente* (lct.tipo_cambio_peritaje-0.2),2) end AS "MONTO_REAL",
            							(case when lct.es_dolares= false then  lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - lct.pagado_cliente  else round((lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - pagado_cliente) * (lct.tipo_cambio_peritaje-0.2),2) end)
            							 -      ((SELECT COALESCE(SUM(CASE WHEN x.es_dolares=false then x.monto else  round(x.monto*x.tipo_cambio,2) end ), 0) FROM liquidacion_costos_reales x
            						  WHERE x.deleted_by IS NULL AND x.num_operacion = lct.num_operacion )  -
            						  case when lct.es_dolares= false then  lct.pagado_cliente  else round(lct.pagado_cliente* (lct.tipo_cambio_peritaje-0.2),2) end)		
            						  AS "MONTO_SALDO_TOTAL",
            						 CASE WHEN lcs.slug IN ('peritaje','costo-de-inspeccion') THEN 'NEGOCIOS' ELSE 'LEGAL' END AS "AREA",
            						 actual.mensaje AS "OBSERVACIONES",
            						 lcs.descripcion AS "CONCEPTO",
									 CASE
            							WHEN lcs.slug = 'asesoria-prestaclub' THEN lct.asesoria_prestaclub
            							WHEN lcs.slug = 'bloqueo-registral' THEN lct.bloqueo_registral
            							WHEN lcs.slug = 'cheque-de-gerencia-extra' THEN lct.cheque_gerencia_extra
            							WHEN lcs.slug = 'costo-de-inspeccion' THEN lct.costo_inspeccion
            							WHEN lcs.slug = 'cri-y-copia-literal' THEN lct.certificado_registral_inmobiliario
            							WHEN lcs.slug = 'derechos-notariales' THEN lct.derechos_notariales
            							WHEN lcs.slug = 'derechos-registrales' THEN lct.derechos_registrales
            							WHEN lcs.slug = 'otorgamiento-de-poder' THEN lct.otorgamiento_poder
            							WHEN lcs.slug = 'peritaje' THEN lct.peritaje
            							WHEN lcs.slug = 'registrales-de-levantamiento' THEN lct.registrales_levantamiento
            							WHEN lcs.slug = 'toma-de-firmas-con-lector-biometrico' THEN lct.toma_firmas_biometrico
            						 END AS "GASTO_SIMULADO",
            						 seg.status AS "ESTADO_POR_AREA",
            						 seg.nombre AS "USUARIO_AREA",
									 CONCAT(actual.descripcion,' (',actual.status,')') AS "ESTADO_ACTUAL",
								 case when coalesce(lcr.updated_by, lcr.created_by)=0 then CONCAT(emp.nombre, ' ',emp.apellido_paterno)  else  emp.nombre end AS "USUARIO_ACTUAL",
								 case when coalesce(lcr.updated_by, lcr.created_by)=0 then coalesce(lcr.updated_at,lcr.created_at)  else  actual.updated_at end AS "FECHA_ACTUALIZADA",
								   coalesce(lcr.monto, 0) as "GASTO_REAL",
            					   round(
            						  case when lct.es_dolares= false then  lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales -  lct.pagado_cliente else round((lct.cri_bloqueo_peritaje + lct.pagos_descontados_cheque + costos_adicionales - lct.pagado_cliente) * (lct.tipo_cambio_peritaje-0.2),2) end
            						 - (sum(coalesce(lcr.monto, 0)) over (
            							   partition by te.codigooperacion
            							   order by lcs.descripcion
            							   rows between unbounded preceding and current row
            						   )-case when lct.es_dolares= false then  lct.pagado_cliente else round(lct.pagado_cliente * (lct.tipo_cambio_peritaje-0.2),2) end),
            						   2
            					   ) as "SALDO"
            					   FROM
            					lcr_conceptos_servicios lcs 
								inner join lcr_servicios ls on
								lcs.id=ls.lcr_conceptos_servicios_id
							   left JOIN liquidacion_costos_reales lcr
									ON ls.id = lcr.lcr_servicios_id		   
							    INNER JOIN liquidacion_costos_transacciones
								lct ON lct.num_operacion = lcr.num_operacion
								inner join tchn_endosado te
            				  	on te.codigooperacion=lcr.num_operacion
							 AND lcr.deleted_by IS NULL
							 LEFT JOIN lcr_solicitud lso ON lso.num_operacion = te.codigooperacion
            					   LEFT JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = lso.id
            					 	inner join usuarios usu on
									COALESCE(lcr.updated_by,lcr.created_by) =usu.id
									inner join empleados emp on
									usu.id_empleado=emp.id
								   	   LEFT JOIN (
            						SELECT
            							lses.lcr_solicitud_id,
            							ls.num_operacion,
            							lse.descripcion,
            							CASE
            								WHEN lses.status = 0 THEN 'PENDIENTE'
            								WHEN lses.status = 1 THEN 'APROBADO'
            								ELSE 'OBSERVADO'
            							END AS status,
            							CONCAT(e.nombre, ' ',e.apellido_paterno) AS nombre,
            							lses.updated_by ,
            							lse.slug
            						FROM lcr_solicitud ls
            						INNER JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = ls.id
            						INNER JOIN lcr_solicitud_etapa lse ON lse.id = lses.lcr_solicitud_etapa_id
            						INNER JOIN usuarios u ON u.id = lses.updated_by
            						INNER JOIN empleados e ON e.id = u.id_empleado
            				) seg ON seg.num_operacion = te.codigooperacion
            							AND seg.slug = CASE WHEN lcs.slug IN ('peritaje','costo-de-inspeccion') THEN 'negocios' ELSE 'legal' END
 							LEFT JOIN (
            						SELECT distinct 
            							ls.num_operacion,
            							lse.descripcion,
            							CASE
            								WHEN lses.status = 0 THEN 'PENDIENTE'
            								WHEN lses.status = 1 THEN 'APROBADO'
            								ELSE 'OBSERVADO'
            							END AS status,
            							CONCAT(e.nombre, ' ',e.apellido_paterno) AS nombre,
            							lses.updated_by,
            							lses.updated_at,
            							(select COALESCE(lsh.mensaje, '')   from lcr_solicitud_history lsh where lsh.lct_solicitud_id = ls.id and status_actual=2 order by id desc limit 1 )as mensaje
            						FROM lcr_solicitud ls
            						INNER JOIN lcr_solicitud_etapa_seguimiento lses ON lses.lcr_solicitud_id = ls.id
            						INNER JOIN lcr_solicitud_etapa lse ON lse.id = lses.lcr_solicitud_etapa_id
            						INNER JOIN usuarios u ON u.id = lses.updated_by
            						INNER JOIN empleados e ON e.id = u.id_empleado
            					 ---  	LEFT JOIN lcr_solicitud_history lsh ON lsh.lct_solicitud_id = ls.id AND lsh.updated_at IS NULL
            						WHERE lses.actual = true
            					   ) actual ON actual.num_operacion = te.codigooperacion
								     WHERE lct.es_desembolso = true and
                                     ( :numOperacion IS NULL or :numOperacion = ''  OR cast(te.codigooperacion as TEXT) = :numOperacion)
                       and te.fechaendoso between  :startDateTime and  :endDateTime
                       and  ( :estado IS NULL or :estado = ''  OR trim(actual.status)  = trim(:estado))
                       GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21
                                              ORDER BY te.codigooperacion, lcs.descripcion
                """, nativeQuery = true)
    List<FlujoTchnReporteProjection> obtenerDetalleCostos(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("numOperacion") String numOperacion, @Param("estado") String estado);



}
