package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.MensajePlantilla;

public interface MensajePlantillaRepository extends JpaRepository<MensajePlantilla,Long> {
    

    @Query("SELECT m FROM MensajePlantilla m  " +
            "join m.mensajeConfiguracion mc " +
            "where m.activo = true and  mc.slug = :slug "+
            "order by m.isDefault desc")
    List<MensajePlantilla> getListaByTipoSlug(@Param("slug") String slug);


    //findByIsDefaultBySlug

    @Query("SELECT m FROM MensajePlantilla m  " +
            "join m.mensajeConfiguracion mc " +
            "where m.activo = true and  mc.slug = :slug and m.isDefault = true")
    MensajePlantilla findByIsDefaultBySlug(@Param("slug") String slug);



}
