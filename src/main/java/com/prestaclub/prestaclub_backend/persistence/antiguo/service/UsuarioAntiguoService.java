package com.prestaclub.prestaclub_backend.persistence.antiguo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.UsuarioDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.mapper.UsuarioMapper;

@Service
public class UsuarioAntiguoService {
    @Autowired
    private UsuarioMapper usuarioMapper;

    // public List<UsuarioDTO> obtenerUsuariosActivos() {
    //     return usuarioMapper.obtenerUsuariosActivos();
    // }   
}
