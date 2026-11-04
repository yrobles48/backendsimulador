package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRConceptosServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRConceptosServiciosRepository;

@Service
public class LCRConceptosServiciosService {
    @Autowired
    private LCRConceptosServiciosRepository lcrConceptosServiciosRepository;

    public List<LCRConceptosServicios> findAll() {
        return lcrConceptosServiciosRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
