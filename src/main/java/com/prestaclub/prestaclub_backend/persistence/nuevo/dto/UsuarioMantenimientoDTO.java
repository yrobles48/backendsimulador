package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioMantenimientoDTO {
    private Long id;
    private String username;
    @JsonProperty("nombre_completo")
    private String nombreCompleto;

    public UsuarioMantenimientoDTO() {}

    public UsuarioMantenimientoDTO(Long id, String username, String nombreCompleto) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    
}
