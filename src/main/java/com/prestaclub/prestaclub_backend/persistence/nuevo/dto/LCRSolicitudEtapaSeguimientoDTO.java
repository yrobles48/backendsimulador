package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;

import lombok.Data;

@Data
public class LCRSolicitudEtapaSeguimientoDTO {

    private List<LCRSolicitudEtapaSeguimiento> etapas;
    private String mensaje;
    private Integer status;

    public LCRSolicitudEtapaSeguimientoDTO() {}
}
