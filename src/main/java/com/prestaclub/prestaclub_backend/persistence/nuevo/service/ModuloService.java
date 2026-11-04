package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.ModuloRepository;

@Service
public class ModuloService {
    @Autowired
    private ModuloRepository moduloRepository;

    public Modulo guardarModulo(Modulo modulo) {
        return moduloRepository.save(modulo);
    }

    public Modulo findBySlug(String slug) {
        return moduloRepository.findBySlug(slug);
    }

    public Modulo findById(Long id) {
        return moduloRepository.findById(id).orElse(null);
    }

    public List<Modulo> findAll(){
        return moduloRepository.findAllByOrderByIdAsc();
    }
    
}
