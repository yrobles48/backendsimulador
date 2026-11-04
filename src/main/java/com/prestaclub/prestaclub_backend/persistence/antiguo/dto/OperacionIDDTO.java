package com.prestaclub.prestaclub_backend.persistence.antiguo.dto;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;

import java.util.List;

public class OperacionIDDTO {

    private Long id;
    private Long numero_operacion;
    private String nombre_cliente;
    private String nombre_corto_cliente;
    private String tipo_documento;
    private String numero_documento;
    private String tipo_operacion;
    private Long tipo_operacion_id;
    private String fase_operacion;
    private Long fase_operacion_id;
    private Long codigo_anterior;
    private boolean es_final_operacion;
    private boolean es_desembolso_operacion;
    private Boolean status_lcr_solicitud;
    private List<LCRSolicitudEtapaSeguimiento> lcr_solicitud_etapa_seguimiento;
    private String email;
    private String telefono;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getNombre_cliente() {
        return nombre_cliente;
    }
    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }
    public String getTipo_documento() {
        return tipo_documento;
    }
    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }
    public String getNumero_documento() {
        return numero_documento;
    }
    public void setNumero_documento(String numero_documento) {
        this.numero_documento = numero_documento;
    } 
    
    public String getTipo_operacion() {
        return tipo_operacion;
    }
    public void setTipo_operacion(String tipo_operacion) {
        this.tipo_operacion = tipo_operacion;
    }
    public String getFase_operacion() {
        return fase_operacion;
    }
    public void setFase_operacion(String fase_operacion) {
        this.fase_operacion = fase_operacion;
    }

    public Long getTipo_operacion_id() {
        return tipo_operacion_id;
    }
    public void setTipo_operacion_id(Long tipo_operacion_id) {
        this.tipo_operacion_id = tipo_operacion_id;
    }
    public Long getFase_operacion_id() {
        return fase_operacion_id;
    }
    public void setFase_operacion_id(Long fase_operacion_id) {
        this.fase_operacion_id = fase_operacion_id;
    }
    public Long getNumero_operacion() {
        return numero_operacion;
    }
    public void setNumero_operacion(Long numero_operacion) {
        this.numero_operacion = numero_operacion;
    }

    public Long getCodigo_anterior() {
        return codigo_anterior;
    }
    public void setCodigo_anterior(Long codigo_anterior) {
        this.codigo_anterior = codigo_anterior;
    }
    public String getNombre_corto_cliente() {
        return nombre_corto_cliente;
    }
    public void setNombre_corto_cliente(String nombre_corto_cliente) {
        this.nombre_corto_cliente = nombre_corto_cliente;
    }
    public boolean isEs_final_operacion() {
        return es_final_operacion;
    }
    public void setEs_final_operacion(boolean es_final_operacion) {
        this.es_final_operacion = es_final_operacion;
    }
    public boolean isEs_desembolso_operacion() {
        return es_desembolso_operacion;
    }
    public void setEs_desembolso_operacion(boolean es_desembolso_operacion) {
        this.es_desembolso_operacion = es_desembolso_operacion;
    }

    public Boolean getStatus_lcr_solicitud() {
        return status_lcr_solicitud;
    }
    public void setStatus_lcr_solicitud(Boolean status_lcr_solicitud) {
        this.status_lcr_solicitud = status_lcr_solicitud;
    }
    
    public List<LCRSolicitudEtapaSeguimiento> getLcr_solicitud_etapa_seguimiento() {
        return lcr_solicitud_etapa_seguimiento;
    }
    public void setLcr_solicitud_etapa_seguimiento(List<LCRSolicitudEtapaSeguimiento> lcr_solicitud_etapa_seguimiento) {
        this.lcr_solicitud_etapa_seguimiento = lcr_solicitud_etapa_seguimiento;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
