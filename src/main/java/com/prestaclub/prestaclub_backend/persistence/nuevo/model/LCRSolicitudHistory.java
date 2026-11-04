package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.math.BigDecimal;
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
@Table(name = "lcr_solicitud_history")
@Data
public class LCRSolicitudHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("status_previo")
    @Column(name = "status_previo")
    private Integer statusPrevio;

    @JsonProperty("status_actual")
    @Column(name = "status_actual")
    private Integer statusActual;
 

    @JsonProperty("monto_simulado")
    @Column(name = "monto_simulado")
    private BigDecimal montoSimulado;

    @JsonProperty("monto_real")
    @Column(name = "monto_real")
    private BigDecimal montoReal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lct_solicitud_id") 
    private LCRSolicitud lctSolicitud;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lct_solicitud_etapa_id") 
    private LCRSolicitudEtapa lctSolicitudEtapa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by") 
    private Usuario createdBy;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by") 
    private Usuario updatedBy;

    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedDate;

    // @Column(name = "es_dolares")
    // @JsonProperty("es_dolares")
    // private Boolean esDolares;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    @JsonProperty("mensaje")
    private String mensaje; 
    


}
