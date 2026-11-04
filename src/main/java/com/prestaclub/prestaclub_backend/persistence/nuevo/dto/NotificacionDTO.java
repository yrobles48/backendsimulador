package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.time.LocalDateTime;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;

public class NotificacionDTO {
    Long id;
    String titulo;
    String descripcion;
    String url;
    boolean leido;
    UsuarioMantenimientoDTO createdBy;
    LocalDateTime createdAt;

    public NotificacionDTO() {}

    public NotificacionDTO(Long id, String titulo, String descripcion, String url, Usuario usuario, LocalDateTime createdAt, boolean leido) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.url = url;
        this.createdBy = new UsuarioMantenimientoDTO();
        this.createdBy.setId(usuario.getId());
        this.createdBy.setNombreCompleto(usuario.getEmpleado().getNombre() + " " + usuario.getEmpleado().getApellidoPaterno() + " " + usuario.getEmpleado().getApellidoMaterno());
        this.createdBy.setUsername(usuario.getUsername());
        this.createdAt = createdAt;
        this.leido = leido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UsuarioMantenimientoDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UsuarioMantenimientoDTO createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    
}
