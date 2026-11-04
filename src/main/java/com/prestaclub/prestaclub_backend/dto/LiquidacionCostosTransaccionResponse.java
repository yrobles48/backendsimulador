package com.prestaclub.prestaclub_backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.PropietariosDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;

public class LiquidacionCostosTransaccionResponse {
    @JsonProperty("operacion")
    private OperacionDTO operacion;
    private LiquidacionCostosTransaccion liquidacionCostosTransaccion;
    private List<PropietariosDTO> propietarios;
    @JsonProperty("LcrSolicitudEtapaSeguimiento")
    private LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimiento;
    private BigDecimal gravamen;
    private int status;

    public LiquidacionCostosTransaccionResponse() {
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public BigDecimal getGravamen() {
        return gravamen;
    }

    public void setGravamen(BigDecimal gravamen) {
        this.gravamen = gravamen;
    }

    public OperacionDTO getOperacion() {
        return operacion;
    }

    public void setOperacion(OperacionDTO operacion) {
        this.operacion = operacion;
    }

    public LiquidacionCostosTransaccion getLiquidacionCostosTransaccion() {
        return liquidacionCostosTransaccion;
    }

    public void setLiquidacionCostosTransaccion(LiquidacionCostosTransaccion liquidacionCostosTransaccion) {
        this.liquidacionCostosTransaccion = liquidacionCostosTransaccion;
    }

    public List<PropietariosDTO> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<PropietariosDTO> propietarios) {
        this.propietarios = propietarios;
    }

    public LCRSolicitudEtapaSeguimiento getLcrSolicitudEtapaSeguimiento() {
        return lcrSolicitudEtapaSeguimiento;
    }

    public void setLcrSolicitudEtapaSeguimiento(LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimiento) {
        this.lcrSolicitudEtapaSeguimiento = lcrSolicitudEtapaSeguimiento;
    }

}
