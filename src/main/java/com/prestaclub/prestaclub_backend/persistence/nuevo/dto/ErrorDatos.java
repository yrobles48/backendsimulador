package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDatos {
    private int id;
    private String texto;
    private Long codigoOperacion;
    private LocalDateTime fechaEndoso;
    private String codigoSunarp;
}