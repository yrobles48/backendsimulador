package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapa;

import lombok.Data;

@Data
public class LcrSolicitudHistoryResponse {

    private Long id;
    private BigDecimal montoPrevio;
    private BigDecimal montoActual; 
    private Integer statusPrevio;
    private Integer statusActual;
    private String mensaje; 
    private UsuarioMantenimientoDTO createdBy;
    private LocalDateTime createdDate;
    private UsuarioMantenimientoDTO updatedBy;
    private LocalDateTime updatedDate;
    private LCRSolicitudEtapa lcrSolicitudEtapa;
    
    public LcrSolicitudHistoryResponse() {}
 
    
}
