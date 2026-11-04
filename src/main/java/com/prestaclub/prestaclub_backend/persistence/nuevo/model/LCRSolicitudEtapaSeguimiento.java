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
import lombok.Data;

@Entity
@Table(name = "lcr_solicitud_etapa_seguimiento")
@Data
public class LCRSolicitudEtapaSeguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lcr_solicitud_etapa_id") 
    private LCRSolicitudEtapa lcrSolicitudEtapa; 

    private Integer status;

    private Boolean actual;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lcr_solicitud_id") 
    private LCRSolicitud lcrSolicitud; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by") 
    private Usuario createdBy;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by") 
    private Usuario updatedBy;

    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
