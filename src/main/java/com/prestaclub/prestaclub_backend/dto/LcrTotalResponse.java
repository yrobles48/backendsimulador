package com.prestaclub.prestaclub_backend.dto;

import java.math.BigDecimal;

public class LcrTotalResponse {
    private String slug;
    private BigDecimal monto;
    private Boolean esDolares;
    private Boolean status;
    private String nombre;
    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public Boolean getEsDolares() {
        return esDolares;
    }
    public void setEsDolares(Boolean esDolares) {
        this.esDolares = esDolares;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    

}
