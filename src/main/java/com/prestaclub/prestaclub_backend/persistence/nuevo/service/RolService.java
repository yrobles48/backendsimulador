package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;

public interface RolService {
    public Rol guardarRol(Rol rol) throws Exception;
    public Rol obtenerRol(Long id) throws Exception;
    public Rol findByNombre(String rol) throws Exception;
    public Rol findById(Long id) throws Exception;
    public List<Rol> findAll() throws Exception;
}
 