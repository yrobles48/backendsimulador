package com.prestaclub.prestaclub_backend.persistence.antiguo.mapper; 
import org.apache.ibatis.annotations.Select;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardListaTipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardTipoSolicitudDTO;

public interface HomeDashboardAntiguoMapper {
    List<DashboardTipoSolicitudDTO> getDashboardTipoSolicitud();
    List<DashboardListaTipoSolicitudDTO> getDashboardListaTipoSolicitud(@Param("tipoSolicitud") Long tipoSolicitud, @Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("filterText") String filterText); 
    Integer getDashboardListaTipoSolicitudTotal(@Param("tipoSolicitud") Long tipoSolicitud, @Param("filterText") String filterText);
}
