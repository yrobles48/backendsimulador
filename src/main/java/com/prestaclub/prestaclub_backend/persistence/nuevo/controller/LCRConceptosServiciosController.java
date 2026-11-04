package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRConceptosServicios; 
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRConceptosServiciosService; 

@RestController
@RequestMapping("/nuevo/lcr-conceptos-servicios")
public class LCRConceptosServiciosController {
    @Autowired
    private LCRConceptosServiciosService lcrConceptosServiciosService;

    @GetMapping("/lista")
    public ResponseEntity<ApiResponseData<List<LCRConceptosServicios>>> listaLcrServicios() {
        ApiResponseData<List<LCRConceptosServicios>> response = new ApiResponseData<>();
        try {
            List<LCRConceptosServicios> lcrConceptosServicios = lcrConceptosServiciosService.findAll();
            response.setData(lcrConceptosServicios);
            response.setSuccess(true);
            response.setMessage("ok");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
