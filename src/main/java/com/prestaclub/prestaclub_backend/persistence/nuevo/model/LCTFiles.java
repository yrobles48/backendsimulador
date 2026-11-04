package com.prestaclub.prestaclub_backend.persistence.nuevo.model;
 

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
@Entity
@Table(name = "lct_files")
public class LCTFiles {

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
    @JoinColumn(name = "lct_id")
    @JsonIgnore
    private LiquidacionCostosTransaccion liquidacionCostosTransaccion;

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

    public LiquidacionCostosTransaccion getLiquidacionCostosTransaccionId() {
        return liquidacionCostosTransaccion;
    }

    public void setLiquidacionCostosTransaccionId(LiquidacionCostosTransaccion liquidacionCostosTransaccion) {
        this.liquidacionCostosTransaccion = liquidacionCostosTransaccion;
    }

    public String getUrlTemporal() {
        return urlTemporal;
    }

    public void setUrlTemporal(String urlTemporal) {
        this.urlTemporal = urlTemporal;
    }

    

}
