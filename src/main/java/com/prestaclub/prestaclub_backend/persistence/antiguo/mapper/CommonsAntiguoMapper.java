package com.prestaclub.prestaclub_backend.persistence.antiguo.mapper;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.TipoSolicitudDTO;

public interface CommonsAntiguoMapper {
    
    List<TipoSolicitudDTO> getTipoSolicitudes();
}
