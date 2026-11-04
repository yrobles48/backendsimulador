package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRServiciosRepository;

@Service
public class LCRServiciosService {
    @Autowired
    private LCRServiciosRepository lcrServiciosRepository;

    public List<LCRServicios> findAll() {
        return lcrServiciosRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public LCRServicios findById(Long id) {
        return lcrServiciosRepository.findById(id).orElse(null);
    }

    public LCRServicios findBySlug(String slug) {
        return lcrServiciosRepository.findBySlug(slug);
    }

    public LCRServicios findBySlugAndLcrConceptosServiciosId(String slug, Long id) {
        return lcrServiciosRepository.findBySlugAndLcrConceptosServiciosId(slug, id);
    }

}
