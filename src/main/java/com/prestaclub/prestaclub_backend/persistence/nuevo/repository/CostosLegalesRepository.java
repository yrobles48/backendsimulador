package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales;

@Repository
public interface CostosLegalesRepository extends JpaRepository<CostosLegales, Integer> {
    @Select("SELECT c FROM CostosLegales c WHERE c.slug = :slug")
    CostosLegales findBySlug(String slug);

    @Select("SELECT c FROM CostosLegales c WHERE c.id = :id")
    CostosLegales findCostosLegalesById(Long id);
}
