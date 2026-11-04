package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Transient;

@Entity
@Table(name = "lcr_solicitud_files")
public class LCRSolicitudFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fileName")
    private String fileName;

    @Column(name = "extension")
    private String extension;

    @Column(name = "url")
    private String url;

    @Column(name = "tag")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "lct_solicitud_id")
    @JsonIgnore
    private LCRSolicitud lcrSolicitud;

    @Transient
    @JsonProperty("url_temporal")
    private String urlTemporal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LCRSolicitud getLcrSolicitud() {
        return lcrSolicitud;
    }

    public void setLcrSolicitud(LCRSolicitud lcrSolicitud) {
        this.lcrSolicitud = lcrSolicitud;
    }

    public String getUrlTemporal() {
        return urlTemporal;
    }

    public void setUrlTemporal(String urlTemporal) {
        this.urlTemporal = urlTemporal;
    }


    
}


