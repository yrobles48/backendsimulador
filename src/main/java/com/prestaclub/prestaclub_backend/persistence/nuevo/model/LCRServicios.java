package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

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
@Table(name = "lcr_servicios")
public class LCRServicios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug")
    @JsonProperty("slug")
    private String slug;

    @Column(name = "descripcion")
    @JsonProperty("descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lcr_conceptos_servicios_id")
    private LCRConceptosServicios lcrConceptosServicios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LCRConceptosServicios getLcrConceptosServicios() {
        return lcrConceptosServicios;
    }

    public void setLcrConceptosServicios(LCRConceptosServicios lcrConceptosServicios) {
        this.lcrConceptosServicios = lcrConceptosServicios;
    }


    
}
