package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolso;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;

import lombok.Data;

@Data
public class LCRReembolsoDTO {
    private LCRReembolso lcrReembolso;
    private LCRSolicitud lcrSolicitud;
    private Date faplicacion;
    private Date fdevolucion;
    private String envio1;
    private BigDecimal montoSimulado;
    private BigDecimal montoReal;
    private BigDecimal saldo;
    private Date fechaenvio1;
    private Date fechaenvio2;
    private Date fenvio;

    public LCRReembolsoDTO(LCRReembolso lcrReembolso, Date faplicacion, Date fdevolucion, String envio1,
            BigDecimal montoSimulado, BigDecimal montoReal, BigDecimal saldo) {
        this.lcrReembolso = lcrReembolso;
        this.faplicacion = faplicacion;
        this.fdevolucion = fdevolucion;
        this.envio1 = envio1;
        this.montoSimulado = montoSimulado;
        this.montoReal = montoReal;
        this.saldo = saldo;
    }

    public LCRReembolsoDTO(LCRSolicitud lcrSolicitud, LCRReembolso lcrReembolso, Date faplicacion, Date fdevolucion,
            String envio1, BigDecimal montoSimulado, BigDecimal montoReal, BigDecimal saldo) {
        this.lcrReembolso = lcrReembolso;
        this.lcrSolicitud = lcrSolicitud;
        this.faplicacion = faplicacion;
        this.fdevolucion = fdevolucion;
        this.envio1 = envio1;
        this.montoSimulado = montoSimulado;
        this.montoReal = montoReal;
        this.saldo = saldo;
    }

    public LCRReembolsoDTO(LCRSolicitud lcrSolicitud, LCRReembolso lcrReembolso, Date faplicacion, Date fdevolucion,
            String envio1, BigDecimal montoSimulado, BigDecimal montoReal, BigDecimal saldo, Date fechaenvio1, Date fechaenvio2) {
        this.lcrReembolso = lcrReembolso;
        this.lcrSolicitud = lcrSolicitud;
        this.faplicacion = faplicacion;
        this.fdevolucion = fdevolucion;
        this.envio1 = envio1;
        this.montoSimulado = montoSimulado;
        this.montoReal = montoReal;
        this.saldo = saldo;
        this.fechaenvio1 = fechaenvio1;
        this.fechaenvio2 = fechaenvio2;

    }

    public LCRReembolsoDTO() {
    }

}
