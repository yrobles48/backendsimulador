package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;
 
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.NotificacionDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;

public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long> {

//     select n.* from notificaciones_usuarios nu
// inner join notificaciones n on  n.id = nu.id
// where nu.usuario_id = 4 ;
@Query("SELECT COUNT(*) FROM Notificaciones n WHERE n.id IN (SELECT nu.notificacion.id FROM NotificacionesUsuarios nu WHERE nu.usuario.id = :userId AND nu.isView = false)")
Integer contadorNotificacionesNoLeidos(Long userId);
    
@Query("""
        SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.NotificacionDTO(
            n.id, n.titulo, n.descripcion, n.url, n.createdBy, n.createdAt, nu.isView
        ) 
        FROM Notificaciones n 
        JOIN NotificacionesUsuarios nu ON n.id = nu.notificacion.id 
        WHERE nu.usuario.id = :userId
        ORDER BY n.createdAt DESC
    """)
    Page<NotificacionDTO> getNotificaciones(@Param("userId") Long userId, Pageable pageable);
}
