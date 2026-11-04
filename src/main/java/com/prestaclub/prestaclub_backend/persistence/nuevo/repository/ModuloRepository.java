package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    @Select("SELECT m FROM Modulo m WHERE m.slug = :slug")
    Modulo findBySlug(String slug);

    List<Modulo> findAllByOrderByIdAsc();
}
