package com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.RolRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.RolService;

import jakarta.transaction.Transactional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public Rol guardarRol(Rol rol)  throws Exception {
        return rolRepository.save(rol);
    }
    @Override
    @Transactional
    public Rol obtenerRol(Long id) throws Exception {
        return rolRepository.GetRolById(id);
    }
    @Override
    public Rol findByNombre(String rol) throws Exception {
        return rolRepository.findByNombre(rol);
    }
    @Override
    public Rol findById(Long id) throws Exception {
        return rolRepository.findByIdentificador(id);
    }
    @Override
    public List<Rol> findAll() throws Exception {
        return rolRepository.findAll();
    }

}
