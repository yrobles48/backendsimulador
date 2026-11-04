package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.CostosLegalesHistoryDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegalesHistory;

@Repository
public interface CostosLegalesHistoryRepository extends JpaRepository<CostosLegalesHistory, Long> {

    // @Query("SELECT l FROM CostosLegalesHistory l WHERE l.tipo = :tipo ORDER BY l.id DESC")
    // Page<CostosLegalesHistory> getListaCostosLegalesHistory( @Param("tipo") String tipo, Pageable pageable);

    @Query("SELECT new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.CostosLegalesHistoryDTO(" +
           "l.oldValor, l.newValor, l.tipo, l.fecha, l.costosLegales.id, l.user.id, " +
           "new com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO(" +
           "l.user.id, l.user.username, " +
           "CONCAT(l.user.empleado.nombre, ' ', l.user.empleado.apellidoPaterno, ' ', COALESCE(l.user.empleado.apellidoMaterno, ''))), " +
           "l.costosLegales) " +
           "FROM CostosLegalesHistory l WHERE (:filterText IS NULL OR :filterText = '' OR l.user.username LIKE CONCAT('%', :filterText, '%') OR l.costosLegales.descripcion LIKE CONCAT('%', :filterText, '%') ) " +
           " AND   l.tipo = :tipo ORDER BY l.id DESC")
    Page<CostosLegalesHistoryDTO> getListaCostosLegalesHistory(
        @Param("tipo") String tipo,
        @Param("filterText") String filterText,
        Pageable pageable
    );

}
