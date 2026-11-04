package com.prestaclub.prestaclub_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;

public class GrabarLiquidacionRequest {

    private Long numOperacion;
    @JsonProperty("liquidacion")
    private LiquidacionCostosTransaccion liquidacionCostosTransaccion;
    @JsonProperty("cronograma")
    private List<LCTCronograma> lctCronograma;

    private Long userId;

    public GrabarLiquidacionRequest() {}

    public Long getNumOperacion() {
        return numOperacion;
    }
    public void setNumOperacion(Long numOperacion) {
        this.numOperacion = numOperacion;
    }
    public LiquidacionCostosTransaccion getLiquidacionCostosTransaccion() {
        return liquidacionCostosTransaccion;
    }
    public void setLiquidacionCostosTransaccion(LiquidacionCostosTransaccion liquidacionCostosTransaccion) {
        this.liquidacionCostosTransaccion = liquidacionCostosTransaccion;
    } 
    public List<LCTCronograma> getLctCronograma() {
        return lctCronograma;
    }
    public void setLctCronograma(List<LCTCronograma> lctCronograma) {
        this.lctCronograma = lctCronograma;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    
}
