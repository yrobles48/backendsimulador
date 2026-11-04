package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mensaje_plantilla")
@Data
public class MensajePlantilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mensaje_tipo_id", nullable = false)
    private MensajeConfiguracion mensajeConfiguracion;

    @Column(nullable = false)
    private String descripcion;

    @Column
    private String subject; // Puede ser nulo para SMS
 
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
