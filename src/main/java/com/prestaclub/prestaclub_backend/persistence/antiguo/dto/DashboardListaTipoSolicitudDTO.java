package com.prestaclub.prestaclub_backend.persistence.antiguo.dto;

public class DashboardListaTipoSolicitudDTO {
    private Long id;
    private String numero_operacion;
    private String nombre_cliente;
    private Long fase_id;
    private String fase;
    private String numero_documento;

    public DashboardListaTipoSolicitudDTO() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumero_operacion() {
        return numero_operacion;
    }
    public void setNumero_operacion(String numero_operacion) {
        this.numero_operacion = numero_operacion;
    }
    public String getNombre_cliente() {
        return nombre_cliente;
    }
    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }
    public Long getFase_id() {
        return fase_id;
    }
    public void setFase_id(Long fase_id) {
        this.fase_id = fase_id;
    }
    public String getFase() {
        return fase;
    }
    public void setFase(String fase) {
        this.fase = fase;
    }
    public String getNumero_documento() {
        return numero_documento;
    }
    public void setNumero_documento(String numero_documento) {
        this.numero_documento = numero_documento;
    }

    
}
