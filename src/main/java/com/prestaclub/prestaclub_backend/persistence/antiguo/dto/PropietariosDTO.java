package com.prestaclub.prestaclub_backend.persistence.antiguo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropietariosDTO {

    private Long id;

    private String nombres;

    @JsonProperty("numero_documento")
    private String numeroDocumento;

    @JsonProperty("es_solicitante")
    private boolean esSolicitante;


    @JsonProperty("correo")
    private String correo;

    public PropietariosDTO() {}

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public boolean isEsSolicitante() {
        return esSolicitante;
    }

    public void setEsSolicitante(boolean esSolicitante) {
        this.esSolicitante = esSolicitante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
