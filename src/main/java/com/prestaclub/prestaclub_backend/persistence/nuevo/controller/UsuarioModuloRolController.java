package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioModuloRolService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("nuevo/usuario-modulo-rol")
@CrossOrigin("*")
public class UsuarioModuloRolController {
    @Autowired
    private UsuarioModuloRolService usuarioModuloRolService;
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Long> requestBody) { 
        try {
            Long userId = requestBody.get("user_id") != null ? requestBody.get("user_id"): null;
            Long moduloId = requestBody.get("modulo_id") != null ? requestBody.get("modulo_id"): null;
            Long rolId = requestBody.get("rol_id") != null ? requestBody.get("rol_id"): null;
            ApiResponse apiResponse = usuarioModuloRolService.guardarNuevoPrivilegio(userId, moduloId, rolId); 
            return ResponseEntity.ok(apiResponse); 
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestBody Map<String, Long> requestBody) { 
        try {
            Long id = requestBody.get("id") != null ? requestBody.get("id"): null;
            ApiResponse apiResponse = usuarioModuloRolService.eliminarPrivilegio(id); 
            return ResponseEntity.ok(apiResponse); 
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }


    
}
