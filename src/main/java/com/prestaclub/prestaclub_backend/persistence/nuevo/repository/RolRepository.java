package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
 

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> { 
    @Query("SELECT r FROM Rol r WHERE r.id = :id")
    Rol GetRolById(@Param("id") Long id);
    
    @Query("SELECT r FROM Rol r WHERE r.nombre = :rol")
    Rol findByNombre(String rol);

    @Query("SELECT r FROM Rol r WHERE r.id = :id")
    Rol findByIdentificador(Long id);

    List<Rol> findAll();
}
