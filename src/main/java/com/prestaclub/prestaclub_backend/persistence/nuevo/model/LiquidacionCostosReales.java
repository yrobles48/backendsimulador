package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "liquidacion_costos_reales")
public class LiquidacionCostosReales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "num_operacion")
    @JsonProperty("num_operacion")
    private Long numOperacion;

    @Column(name = "kardex")
    @JsonProperty("kardex")
    private Long kardex;

    @Column(name = "referencia")
    @JsonProperty("referencia")
    private String referencia;

    @Column(name = "es_dolares")
    @JsonProperty("es_dolares")
    private Boolean esDolares;

    @Column(name = "tipo_cambio" , precision = 38, scale = 3)
    @JsonProperty("tipo_cambio")
    private BigDecimal tipoCambio;

    //join con lccrservicios
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lcr_servicios_id")
    private LCRServicios lcrServicios;

    @Column(name = "monto")
    @JsonProperty("monto")
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by") 
    private Usuario createdBy; 

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by") 
    private Usuario updatedBy;

    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deleted_by") 
    private Usuario deletedBy;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    public LiquidacionCostosReales() {
    }

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

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Usuario getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Usuario updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Usuario getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Usuario deletedBy) {
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
