package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

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

@Entity
@Table(name = "notificaciones")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    @JsonProperty("titulo")
    private String titulo;

    @Column(name = "descripcion", nullable = false)
    @JsonProperty("descripcion")
    private String descripcion;
    
    @Column(name = "url", nullable = false)
    @JsonProperty("url")
    private String url;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by") 
    private Usuario createdBy; 

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;



    public Notificaciones() {}



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



    public Usuario getCreatedBy() {
        return createdBy;
    }



    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}
