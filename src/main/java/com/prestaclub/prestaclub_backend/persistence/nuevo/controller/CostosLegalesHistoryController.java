package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.CostosLegalesHistoryService; 

@RestController
@RequestMapping("nuevo/costos-legales-history")
public class CostosLegalesHistoryController {
    @Autowired
    private CostosLegalesHistoryService costosLegalesHistoryService;

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> listaCostosLegalesHistory(@RequestBody Map<String, String> requestBody) {
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber")) : 1;
        String filterText = requestBody.containsKey("filterText") ? requestBody.get("filterText") : "";
        String tipo = requestBody.containsKey("tipo") ? requestBody.get("tipo") : "";
        ListaResponse costosLegalesHistory = costosLegalesHistoryService.listaCostosLegalesHistory(pageSize, pageNumber, filterText, tipo);
        return ResponseEntity.ok(costosLegalesHistory);
    }
    
 
}
