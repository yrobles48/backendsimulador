package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "rpt_mensaje")
@Data
public class RptMensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_operacion")
    private Long num_operacion;

    @Column(name = "correo")
    private String correo;

    @Column(name = "asunto")
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "envio1")
    private String envio1;

    @Column(name = "fechaenvio1")
    private Date fechaenvio1;

    @Column(name = "fechaenvio2")
    private Date fechaenvio2;

    @Column(name = "estado")
    private String estado;

    @Column(name = "activo")
    private Boolean activo;


    @Column(name = "cuentabancaria")
    private String cuentabancaria;


    @Column(name = "datosadicional")
    private String datosadicional;


    @Column(name = "copropietario")
    private String copropietario;

    @Column(name = "msgestado")
    private Boolean msgestado;


    @Column(name = "msgerror")
    private String msgerror;


    @Column(name = "msgfecha")
    private Date msgfecha;

    @Column(name = "fdevolucion")
    private Date fdevolucion;

    @Column(name = "created_By")
    private Long created_By;

    @Column(name = "dcreate_by")
    private Date dcreate_by;

    @Column(name = "modify_by")
    private Long modify_by;

    @Column(name = "dmodify_by")
    private Date dmodify_by;


    @Column(name = "nsaldo")
    private BigDecimal nsaldo;


    @Column(name = "es_dolares")
    private Boolean es_dolares;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "faplicacion")
    private Date faplicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reembolso") // nombre de la FK en la tabla actual
    private LCRReembolso idreembolso;

    @Column(name = "banco")
    private String banco;

    @Column(name = "documento")
    private String documento;

    @Column(name = "esconta",  columnDefinition = "boolean default false")
    private Boolean esconta;

}
