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
import jakarta.persistence.Transient;

@Entity
@Table(name = "costos_legales_history")
public class CostosLegalesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("old_valor")
    @Column(name = "old_valor")
    private BigDecimal oldValor;

    @JsonProperty("new_valor")
    @Column(name = "new_valor")
    private BigDecimal newValor;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "costos_legales_id")
    private CostosLegales costosLegales;
 
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "user_id")
    private Usuario user;
    // @Transient
    // @JsonProperty("created_by_name")
    // private UsuarioMantenimientoDTO createdByName; 

    public CostosLegalesHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // public Long getCostosLegalesId() {
    //     return costosLegalesId;
    // }

    // public void setCostosLegalesId(Long costosLegalesId) {
    //     this.costosLegalesId = costosLegalesId;
    // } 

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    } 

    public CostosLegales getCostosLegales() {
        return costosLegales;
    }

    public void setCostosLegales(CostosLegales costosLegales) {
        this.costosLegales = costosLegales;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
 
    
    
}
