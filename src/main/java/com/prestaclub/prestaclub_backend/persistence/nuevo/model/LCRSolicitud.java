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
@Table(name = "lcr_solicitud")
public class LCRSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonProperty("status")
    // @Column(name = "status")
    // private Integer status;

    @Column(name = "num_operacion")
    @JsonProperty("num_operacion")
    private Long numOperacion;

    // @Column(name = "cuenta_bancaria")
    // @JsonProperty("cuenta_bancaria")
    // private String cuentaBancaria;

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

    @Column(name = "monto_simulado")
    @JsonProperty("monto_simulado")
    private BigDecimal montoSimulado;

    @Column(name = "monto_real")
    @JsonProperty("monto_real")
    private BigDecimal montoReal;

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "lcr_solicitud_etapa_id") 
    // private LCRSolicitudEtapa lcrSolicitudEtapa; 

    @Column(name = "es_dolares")
    @JsonProperty("es_dolares")
    private Boolean esDolares;

    // @Column(name = "cci" , nullable = true)
    // @JsonProperty("cci")
    // private String cci;

    public LCRSolicitud() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public BigDecimal getMontoSimulado() {
        return montoSimulado;
    }

    public void setMontoSimulado(BigDecimal montoSimulado) {
        this.montoSimulado = montoSimulado;
    }

    public BigDecimal getMontoReal() {
        return montoReal;
    }

    public void setMontoReal(BigDecimal montoReal) {
        this.montoReal = montoReal;
    }

    public Long getNumOperacion() {
        return numOperacion;
    }

    public void setNumOperacion(Long numOperacion) {
        this.numOperacion = numOperacion;
    }

    // public String getCuentaBancaria() {
    //     return cuentaBancaria;
    // }

    // public void setCuentaBancaria(String cuentaBancaria) {
    //     this.cuentaBancaria = cuentaBancaria;
    // }

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

    // public Integer getStatus() {
    //     return status;
    // }

    // public void setStatus(Integer status) {
    //     this.status = status;
    // }

    public Boolean getEsDolares() {
        return esDolares;
    }

    public void setEsDolares(Boolean esDolares) {
        this.esDolares = esDolares;
    }

    // public String getCci() {
    //     return cci;
    // }

    // public void setCci(String cci) {
    //     this.cci = cci;
    // }

    
}
