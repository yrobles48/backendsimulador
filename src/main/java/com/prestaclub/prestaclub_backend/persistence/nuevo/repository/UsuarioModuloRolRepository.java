package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;

@Repository
public interface UsuarioModuloRolRepository extends JpaRepository<UsuarioModuloRol, Long> {
    List<UsuarioModuloRol> findByUsuarioId(Long id);

    @Query("SELECT u.usuario FROM UsuarioModuloRol u WHERE u.modulo.id = :moduloId")
    List<Usuario> findByModuloId(@Param("moduloId") Long moduloId);
}
