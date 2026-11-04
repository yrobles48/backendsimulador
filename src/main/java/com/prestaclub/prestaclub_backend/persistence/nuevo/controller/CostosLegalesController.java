package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.CostosLegalesService;

@RestController
@RequestMapping("nuevo/costos-legales")
public class CostosLegalesController {  
    @Autowired
    private CostosLegalesService costosLegalesService;

    @GetMapping("/lista")
    public ResponseEntity<ApiResponseData<List<CostosLegales>>> listar() {
        ApiResponseData<List<CostosLegales>> apiResponseData = new ApiResponseData<>();
        try {
            List<CostosLegales> costosLegales = costosLegalesService.listar();
            apiResponseData.setData(costosLegales);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
        return ResponseEntity.ok(apiResponseData);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<ApiResponse> actualizar(@RequestBody Map<String, String> requestBody) { 
        Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id")): null;
        BigDecimal valor = requestBody.get("valor") != null ? BigDecimal.valueOf(Double.parseDouble(requestBody.get("valor"))): null;
        String slug = requestBody.get("slug") != null ? requestBody.get("slug"): null;
        Long userId = requestBody.get("user_id") != null ? Long.parseLong(requestBody.get("user_id")): null;
        String tipo = requestBody.get("tipo") != null ? requestBody.get("tipo"): null;
        ApiResponse resp = costosLegalesService.actualizar(id, valor, slug,tipo, userId);
        if (resp.isSuccess()) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body(resp);
        }
    }

}
