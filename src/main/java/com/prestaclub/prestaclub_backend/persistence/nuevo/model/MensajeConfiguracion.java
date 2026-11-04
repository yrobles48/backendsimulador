package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

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
@Table(name = "mensaje_configuracion")
@Data
public class MensajeConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false, unique = true)
    private String slug;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mensaje_tipo_id", nullable = false)
    private MensajeTipo mensajeTipo;


}
