package com.prestaclub.prestaclub_backend.persistence.nuevo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
@Entity
@Table(name = "rpt_files")
public class RptFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "created_by")
    private String created_by;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "date_create")
    private Date date_create;


    @Transient
    @JsonProperty("url_temporal")
    private String urlTemporal;


    @Column(name = "num_operacion")
    private Long num_operacion;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reembolso") // nombre de la FK en la tabla actual
    private LCRReembolso idreembolso;

}
