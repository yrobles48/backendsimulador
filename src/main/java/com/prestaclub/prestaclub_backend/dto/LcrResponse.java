package com.prestaclub.prestaclub_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;

public class LcrResponse {
    private Long id;
    private Long numOperacion;
    private Long kardex;
    private String referencia;
    private Boolean esDolares;
    private BigDecimal tipoCambio;
    private LCRServicios lcrServicios;
    private BigDecimal monto;
    private UsuarioMantenimientoDTO createdBy;
    private LocalDateTime createdAt;
    private UsuarioMantenimientoDTO updatedBy;
    private LocalDateTime updatedAt;
    private UsuarioMantenimientoDTO deletedBy;
    private LocalDateTime deletedAt;

    public LcrResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumOperacion() {
        return numOperacion;
    }

    public void setNumOperacion(Long numOperacion) {
        this.numOperacion = numOperacion;
    }

    public Long getKardex() {
        return kardex;
    }

    public void setKardex(Long kardex) {
        this.kardex = kardex;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Boolean getEsDolares() {
        return esDolares;
    }

    public void setEsDolares(Boolean esDolares) {
        this.esDolares = esDolares;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

 

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public UsuarioMantenimientoDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UsuarioMantenimientoDTO createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UsuarioMantenimientoDTO getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UsuarioMantenimientoDTO updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UsuarioMantenimientoDTO getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UsuarioMantenimientoDTO deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LCRServicios getLcrServicios() {
        return lcrServicios;
    }

    public void setLcrServicios(LCRServicios lcrServicios) {
        this.lcrServicios = lcrServicios;
    }

    
}
