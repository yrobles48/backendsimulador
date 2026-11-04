package com.prestaclub.prestaclub_backend.persistence.antiguo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.TipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.mapper.CommonsAntiguoMapper;

@Service
public class CommonsAntiguoService {

    @Autowired
    CommonsAntiguoMapper commonsAntiguoMapper;
    public List<TipoSolicitudDTO> getTipoSolicitudes() {
        return commonsAntiguoMapper.getTipoSolicitudes();
    }
}
