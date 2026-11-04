package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoSubEstados;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface LCRReembolsoSubEstadosRepository extends JpaRepository<LCRReembolsoSubEstados, Long> {
        LCRReembolsoSubEstados findBySlug(String slug);


        @Modifying
        @Transactional
        @Query(value = "update lcr_reembolso set lcr_reembolso_sub_estados_id= :lsestado  where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizaReembolso(@Param("nrooperacion") Long nrooperacion,
                        @Param("estado") Long lsestado);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set faplicacion= :ldFecha  where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizafaplicacion(@Param("nrooperacion") Long nrooperacion,
                        @Param("fecha") Date ldFecha);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set envio1= 'En Consulta'  where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizafaplicacionConsulta(@Param("nrooperacion") Long nrooperacion);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set envio1= 'Con Cuenta'  where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizaCuenta(@Param("nrooperacion") Long nrooperacion);


        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set envio1= 'En Consulta',esconta=false  where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizafcontabilidad(@Param("nrooperacion") Long nrooperacion);



        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set  esconta=false  where num_operacion= :nrooperacion ", nativeQuery = true)
        void soloFlag(@Param("nrooperacion") Long nrooperacion);
}
