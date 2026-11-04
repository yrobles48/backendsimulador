package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Banco;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.RptFiles;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.RptMensaje;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RptMensajeRepository extends JpaRepository<RptMensaje, Long> {
        Optional<RptMensaje> findById(Long id);

        // Opción 2: también funciona
        RptMensaje getById(Long id);

        @Query("SELECT r FROM RptMensaje r WHERE r.num_operacion = :numOperacion and activo=true")
        RptMensaje findByNumOperacion(@Param("numOperacion") Long numOperacion);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO rpt_mensaje (activo, asunto, contenido, correo, envio1, estado, " +
                        "fechaenvio1,num_operacion,created_by,dcreate_by,es_dolares,nsaldo,telefono,id_reembolso,documento) "
                        +
                        "VALUES (true, :asunto, :contenido, :correo, 'primer envio', '1', CURRENT_DATE,:nrooperacion,:UserId, CURRENT_DATE,:esDolares,:nsaldo,:telefono,:idReembolso,:documento)", nativeQuery = true)
        void insertaRtaMensaje(@Param("idReembolso") Long idReembolso,
                        @Param("asunto") String asunto,
                        @Param("contenido") String contenido,
                        @Param("correo") String correo,
                        @Param("nrooperacion") Long nrooperacion,
                        @Param("UserId") Long UserId,
                        @Param("esDolares") Boolean esDolares,
                        @Param("nsaldo") Double nsaldo,
                        @Param("telefono") String telefono,
                        @Param("documento") String documento);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set banco= :banco, cuentabancaria= :cuentaban ,correo= :correo1,"+
                " copropietario= :copro , datosadicional= :adicional,esconta=false,modify_by=:modify_by,dmodify_by=CURRENT_DATE where num_operacion= :nrooperacion ", nativeQuery = true)
        void actualizaRtaMensaje(@Param("nrooperacion") Long nrooperacion,
                        @Param("Banco") String banco,
                        @Param("ctaban") String cuentaban,
                        @Param("copro") String copro,
                        @Param("correo") String correo1,
                        @Param("adicion") String adicional,
                        @Param("modify_by") Long modify_by);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set  fechaenvio2 = CASE WHEN esconta IS TRUE THEN current_date ELSE " +
                "case when fechaenvio2 is not NULL then current_date else null end    end where num_operacion= :nrooperacion ", nativeQuery = true)
        void actuSegundMensaje(@Param("nrooperacion") Long nrooperacion);


        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set fdevolucion=:fdevol,modify_by=:UserId,dmodify_by= current_date where num_operacion= :nrooperacion   and activo=true", nativeQuery = true)
        void actualizaFDevol(@Param("nrooperacion") Long nrooperacion,
                        @Param("fdevol") Date fdevol,
                        @Param("UserId") Long UserId);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set envio1=null,msgestado= null, msgerror=null ,msgfecha = null,correo=:lscorreo,"
                        +
                        "modify_by=:UserId,dmodify_by= current_date where num_operacion= :nrooperacion and activo=true", nativeQuery = true)
        void actualizaCorreo(@Param("nrooperacion") Long nrooperacion,
                        @Param("correo") String lscorreo,
                        @Param("UserId") Long UserId);

        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set msgestado= :estado , msgerror= :error ,msgfecha = CURRENT_DATE," +
                        "fechaenvio2 =case when :nsaldo1>0 then null else current_date end, modify_by=:modify,dmodify_by= current_date,"
                        +
                        "envio1=case when fechaenvio2 is not null then 'segundo envio'  when :estado =false then 'error de envio' else 'primer envio' end"
                        +
                        "  where num_operacion= :nrooperacion  and activo=true", nativeQuery = true)
        void actualizaEstadoCorreo(@Param("nrooperacion") Long nrooperacion,
                        @Param("msgestado") Boolean estado,
                        @Param("msgerror") String error,
                        @Param("modify_by") Long modify,
                        @Param("nsaldo") Double nsaldo1);


        @Modifying
        @Transactional
        @Query(value = "update rpt_mensaje set rectificado='1'    "         +
                "  where num_operacion= :nrooperacion  and activo=true", nativeQuery = true)
        void rectificaEstadoCorreo(@Param("nrooperacion") Long nrooperacion);

        @Modifying
        @Transactional
        @Query(value = "insert into rpt_files(num_operacion,filename,estado,date_create,created_by,id_reembolso) " +
                        " values (:nrooperacion,:rutafile,true,CURRENT_DATE,:UserId,:reembolsoId)", nativeQuery = true)
        void ins_rpt_files(@Param("reembolsoId") Long reembolsoId,
                        @Param("nrooperacion") Long nrooperacion,
                        @Param("rutafile") String rutafile,
                        @Param("UserId") Long UserId);

        @Query("SELECT r FROM RptFiles r WHERE r.num_operacion = :nrooperacion and estado=true")
        List<RptFiles> lista_rpt_files(@Param("nrooperacion") Long nrooperacion);

        @Modifying
        @Transactional
        @Query(value = "update rpt_files set estado=false  WHERE id= :idope and num_operacion = :nrooperacion and estado=true", nativeQuery = true)
        void eliminar_rptfiles(@Param("id") Long idope,
                        @Param("nrooperacion") Long nrooperacion);

        @Query(value = "SELECT * FROM banco", nativeQuery = true)
        List<Object[]> listarbanco();

        @Modifying
        @Transactional
        @Query(value = "insert into  rpt_mensaje_history(id, activo,  created_by,  datosadicional," +
                        "estado,num_operacion, id_reembolso,dcreate_by) values " +
                        "( :pid, :pactivo,  :pcreated_by,  :pdatosadi," +
                        ":pestado,:pnrooperacion, :pid_reembolso,CURRENT_DATE)", nativeQuery = true)
        void actualizaRtaMensajeHistorico(
                        @Param("pid") Long pid,
                        @Param("pactivo") Boolean pactivo,
                        @Param("pcreated_by") Long pcreated_by,
                        @Param("pdatosadi") String pdatosadi,
                        @Param("pestado") String pestado,
                        @Param("pnrooperacion") Long pnrooperacion,
                        @Param("pid_reembolso") Long pid_reembolso);

}
