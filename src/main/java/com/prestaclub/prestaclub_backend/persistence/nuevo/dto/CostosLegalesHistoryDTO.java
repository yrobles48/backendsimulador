package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales; 

public class CostosLegalesHistoryDTO {
    
    @JsonProperty("old_valor")
    private BigDecimal oldValor;

    @JsonProperty("new_valor") 
    private BigDecimal newValor;
 
    private String tipo;
 
    private LocalDateTime fecha;

    @JsonProperty("costos_legales_id") 
    private Long costosLegalesId; 

    @JsonProperty("user_id") 
    private Long userId;

    @JsonProperty("created_by_name")
    private UsuarioMantenimientoDTO user; 
 
    @JsonProperty("costos_legales")
    private CostosLegales costosLegales;


    public CostosLegalesHistoryDTO() {}

    public CostosLegalesHistoryDTO(BigDecimal oldValor, BigDecimal newValor, String tipo, LocalDateTime fecha,
            Long costosLegalesId, Long userId , UsuarioMantenimientoDTO user, CostosLegales costosLegales) {
        this.oldValor = oldValor;
        this.newValor = newValor;
        this.tipo = tipo;
        this.fecha = fecha;
        this.costosLegalesId = costosLegalesId;
        this.userId = userId;
        this.user = user;
        this.costosLegales = costosLegales;
    }

    public BigDecimal getOldValor() {
        return oldValor;
    }

    public void setOldValor(BigDecimal oldValor) {
        this.oldValor = oldValor;
    }

    public BigDecimal getNewValor() {
        return newValor;
    }

    public void setNewValor(BigDecimal newValor) {
        this.newValor = newValor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getCostosLegalesId() {
        return costosLegalesId;
    }

    public void setCostosLegalesId(Long costosLegalesId) {
        this.costosLegalesId = costosLegalesId;
    }

    public UsuarioMantenimientoDTO getUser() {
        return user;
    }

    public void setUser(UsuarioMantenimientoDTO user) {
        this.user = user;
    }

    public CostosLegales getCostosLegales() {
        return costosLegales;
    }

    public void setCostosLegales(CostosLegales costosLegales) {
        this.costosLegales = costosLegales;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    


}
