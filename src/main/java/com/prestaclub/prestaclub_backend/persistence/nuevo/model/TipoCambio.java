package com.prestaclub.prestaclub_backend.persistence.nuevo.model;
 
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_cambio")
public class TipoCambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "moneda_base", nullable = false)
    private String monedaBase;

    @Column(name = "moneda_destino", nullable = false)
    private String monedaDestino;

    @Column(name = "tipo_cambio", nullable = false, precision = 38, scale = 3 )
    private BigDecimal tipoCambio;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha; 

    public TipoCambio() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonedaBase() {
        return monedaBase;
    }

    public void setMonedaBase(String monedaBase) {
        this.monedaBase = monedaBase;
    }

    public String getMonedaDestino() {
        return monedaDestino;
    }

    public void setMonedaDestino(String monedaDestino) {
        this.monedaDestino = monedaDestino;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

 
    
}
