package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;

import jakarta.transaction.Transactional;

public interface NotificacionesUsuariosRepository extends JpaRepository<NotificacionesUsuarios, Long> {
    //select * from notificaciones_usuarios nu where nu.usuario_id = 11 and nu.notificacion_id = 1 ; 

    @Query(value = "select * from notificaciones_usuarios nu where nu.usuario_id = :userId and nu.notificacion_id = :notificacionId", nativeQuery = true)
    NotificacionesUsuarios findByUserIdAndNotificacionId(@Param("userId") Long userId, @Param("notificacionId") Long notificacionId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notificaciones_usuarios SET leido = true WHERE usuario_id = :userId", nativeQuery = true)
    int allViews(@Param("userId") Long userId);
}
