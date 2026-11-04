package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudFiles;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class LcrSolicitudResponse {

    private Long id;
    private Long numOperacion;
    private Integer status;
    // private UsuarioMantenimientoDTO solicitante;
    // private LocalDateTime fechaSolicitud;
    private BigDecimal montoSimulado;
    private BigDecimal montoReal;
    // private Boolean esDolares;
    // private String mensaje;

    // private String cuentaBancaria;
    // private List<LCRSolicitudFiles> files;
    private LocalDateTime createdAt;
    private Boolean esDolares;

    public LcrSolicitudResponse(
            Long id,
            Long numOperacion,
            Integer status,
            BigDecimal montoSimulado,
            BigDecimal montoReal,
            LocalDateTime createdAt,
            Boolean esDolares) {
        this.id = id;
        this.numOperacion = numOperacion;
        this.status = status;
        this.montoSimulado = montoSimulado;
        this.montoReal = montoReal;
        this.createdAt = createdAt;
        this.esDolares = esDolares;
    }

    public LcrSolicitudResponse() {
    }
}
