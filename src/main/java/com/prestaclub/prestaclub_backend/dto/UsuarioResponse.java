package com.prestaclub.prestaclub_backend.dto; 

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Empleado;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;

public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;
    private boolean active;
    // private Rol rol; 
    private Empleado empleado;
    private boolean notificacionCorreo;
    @JsonProperty("privilegios")
    private List<UsuarioModuloRol> usuarioModuloRol;

    private UsuarioMantenimientoDTO userCreation;
    private LocalDateTime createdDate;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<UsuarioModuloRol> getUsuarioModuloRol() {
        return usuarioModuloRol;
    }

    public void setUsuarioModuloRol(List<UsuarioModuloRol> usuarioModuloRol) {
        this.usuarioModuloRol = usuarioModuloRol;
    }

    public UsuarioMantenimientoDTO getUserCreation() {
        return userCreation;
    }

    public void setUserCreation(UsuarioMantenimientoDTO userCreation) {
        this.userCreation = userCreation;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isNotificacionCorreo() {
        return notificacionCorreo;
    }

    public void setNotificacionCorreo(boolean notificacionCorreo) {
        this.notificacionCorreo = notificacionCorreo;
    }

    
 
    
}
