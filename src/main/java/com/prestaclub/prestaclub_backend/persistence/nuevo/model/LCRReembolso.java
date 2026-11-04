package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lcr_reembolso")
@Data
public class LCRReembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_operacion")
    @JsonProperty("num_operacion")
    private Long numOperacion;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lcr_reembolso_sub_estados_id") 
    private LCRReembolsoSubEstados lcrReembolsoSubEstados;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_aprobacion_id") 
    private Usuario usuarioAprobacion; 

    @Column(name = "fecha_aprobacion")
    @JsonProperty("fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by") 
    private Usuario createdBy; 

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;


    @Transient
    @JsonProperty("es_dolares")
    private Boolean es_dolares;

    @Transient
    @JsonProperty("flag")
    private Boolean flag;

}
