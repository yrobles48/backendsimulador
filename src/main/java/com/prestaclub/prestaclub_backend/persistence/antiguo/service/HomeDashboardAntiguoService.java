package com.prestaclub.prestaclub_backend.persistence.antiguo.service;
import org.springframework.data.domain.Page;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardListaTipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardTipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.mapper.HomeDashboardAntiguoMapper; 


@Service
public class HomeDashboardAntiguoService {
    @Autowired
    private HomeDashboardAntiguoMapper homeDashboardAntiguoMapper;

    public List<DashboardTipoSolicitudDTO> getDashboardTipoSolicitud() {
        return homeDashboardAntiguoMapper.getDashboardTipoSolicitud();
    }

    // public List<DashboardListaTipoSolicitudDTO> getDashboardListaTipoSolicitud(Long tipoSolicitud) {
    //     return homeDashboardAntiguoMapper.getDashboardListaTipoSolicitud(tipoSolicitud);
    // }

    public ListaResponse getDashboardListaTipoSolicitud(Long tipoSolicitud, Integer pageSize, Integer pageNumber, String filterText) {
        int offset = (pageNumber - 1) * pageSize;
        List<DashboardListaTipoSolicitudDTO> dashboardListaTipoSolicitud = homeDashboardAntiguoMapper.getDashboardListaTipoSolicitud(tipoSolicitud, pageSize, offset, filterText);
        ListaResponse response = new ListaResponse();
        response.setData(dashboardListaTipoSolicitud);
        response.setTotal((int)homeDashboardAntiguoMapper.getDashboardListaTipoSolicitudTotal(tipoSolicitud , filterText));
        response.setPageNumber(pageNumber);  // Página actual
        response.setPageSize(pageSize); 
        response.setSuccess(true);
        response.setMessage("ok");
        return response;
        // return homeDashboardAntiguoMapper.getDashboardListaTipoSolicitud(tipoSolicitud, pageSize, pageNumber);
    }
}
