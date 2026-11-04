package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import lombok.Data;

@Data
public class LCRMontoEstadosDTO {
    private Long pendiente;
    private Long aprobado;
    private Long observado;

    public LCRMontoEstadosDTO() {}

    public LCRMontoEstadosDTO(Long pendiente, Long aprobado, Long observado) {
        this.pendiente = pendiente;
        this.aprobado = aprobado;
        this.observado = observado;
    }
}
