package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo; 
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.ModuloService;

@RestController
@RequestMapping("nuevo/modulo")
@CrossOrigin("*")
public class ModuloController {
    @Autowired
    private ModuloService moduloService;

    @GetMapping("/lista")
    public ResponseEntity<ApiResponseData<List<Modulo>>> getModuloLista() {
        ApiResponseData<List<Modulo>> apiResponseData = new ApiResponseData<>();
        try {
            List<Modulo> modulos = moduloService.findAll();
            apiResponseData.setData(modulos);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
        return ResponseEntity.ok(apiResponseData);
    }
}
