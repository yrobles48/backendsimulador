package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRServiciosService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/nuevo/lcr-servicios")
public class LCRServiciosController {

    @Autowired
    private LCRServiciosService lcrServiciosService;

    @GetMapping("/lista")
    public ResponseEntity<ApiResponseData<List<LCRServicios>>> listaLcrServicios() {
        ApiResponseData<List<LCRServicios>> response = new ApiResponseData<>();
        try {
            List<LCRServicios> lcrServicios = lcrServiciosService.findAll();
            response.setData(lcrServicios);
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
