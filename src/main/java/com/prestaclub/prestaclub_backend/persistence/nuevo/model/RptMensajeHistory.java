package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "rpt_mensaje_history")
@Data
public class RptMensajeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_By")
    private Long created_By;

    @Column(name = "dcreate_by")
    private Date dcreate_by;


    @Column(name = "datosadicional")
    private String datosadicional;

    @Column(name = "estado")
    private String estado;

    @Column(name = "num_operacion")
    private Long num_operacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reembolso") // nombre de la FK en la tabla actual
    private LCRReembolso idreembolso;

}
