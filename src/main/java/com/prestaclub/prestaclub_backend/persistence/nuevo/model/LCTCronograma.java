package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "lct_cronograma")
public class LCTCronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("nro_cuota")
    @Column(name = "nro_cuota")
    private Long nroCuota;
    @JsonProperty("fecha_pago")
    @Column(name = "fecha_pago")
    private String fechaPago;
    @Column(name = "capital")
    private BigDecimal capital;
    @Column(name = "interes")
    private BigDecimal interes;
    @JsonProperty("cuota_depositar")
    @Column(name = "cuota_depositar")
    private BigDecimal cuotaDepositar;
    @Column(name = "saldo")
    private BigDecimal saldo;
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "liquidacion_costos_transaccion_id")
    private LiquidacionCostosTransaccion liquidacionCostosTransaccionId;


    public LCTCronograma() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getNroCuota() {
        return nroCuota;
    }


    public void setNroCuota(Long nroCuota) {
        this.nroCuota = nroCuota;
    }


    public String getFechaPago() {
        return fechaPago;
    }


    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }


    public BigDecimal getCapital() {
        return capital;
    }


    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }


    public BigDecimal getInteres() {
        return interes;
    }


    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }


    public BigDecimal getCuotaDepositar() {
        return cuotaDepositar;
    }


    public void setCuotaDepositar(BigDecimal cuotaDepositar) {
        this.cuotaDepositar = cuotaDepositar;
    }


    public BigDecimal getSaldo() {
        return saldo;
    }


    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }


    public LiquidacionCostosTransaccion getLiquidacionCostosTransaccionId() {
        return liquidacionCostosTransaccionId;
    }


    public void setLiquidacionCostosTransaccionId(LiquidacionCostosTransaccion liquidacionCostosTransaccionId) {
        this.liquidacionCostosTransaccionId = liquidacionCostosTransaccionId;
    }
    
    
} 