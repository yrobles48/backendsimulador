package com.prestaclub.prestaclub_backend.persistence.antiguo.dto;

public class DashboardTipoSolicitudDTO extends TipoSolicitudDTO { 
    private Long quantity;

    public DashboardTipoSolicitudDTO() {}
 
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    
}
