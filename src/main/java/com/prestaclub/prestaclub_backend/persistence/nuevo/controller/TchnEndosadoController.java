package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LogErrorService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.TchnEndosadoService;

@RestController
@RequestMapping("nuevo/tchn-endosado")
public class TchnEndosadoController {
    @Autowired
    private TchnEndosadoService tchnEndosadoService;

    @Autowired
    private LogErrorService logErrorService;

    @PostMapping("/subida-masiva")  
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        ApiResponse apiResponse = new ApiResponse();
        if (file.isEmpty()) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("El archivo esta vacio");
            return ResponseEntity.badRequest().body(apiResponse);
        }
        try {
            if(userId == null) return ResponseEntity.badRequest().body(new ApiResponse("El userId es requerido", false));

            apiResponse = tchnEndosadoService.cargaMasiva(file, userId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            logErrorService.guardar(e, "nuevo/tchn-endosado/subida-masiva");
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al subir archivo " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
