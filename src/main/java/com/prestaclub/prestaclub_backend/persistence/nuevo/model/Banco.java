package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banco")
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo")
    private Long codigo;



}
