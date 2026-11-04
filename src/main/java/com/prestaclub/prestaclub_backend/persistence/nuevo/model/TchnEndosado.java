package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tchn_endosado")
@Data
public class TchnEndosado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long codigoOperacion;
    private LocalDateTime fechaEndoso;
    private String codigoSunarp;
}
