package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRSolicitudEtapaSeguimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudEtapaSeguimientoService;

@RestController
@RequestMapping("/nuevo/lcr-solicitud-etapa-seguimiento")
public class LCRSolicitudEtapaSeguimientoController {

    @Autowired
    private LCRSolicitudEtapaSeguimientoService lcrSolicitudEtapaSeguimientoService;

    @GetMapping("/{num_operacion}")
    public ResponseEntity<ApiResponseData<LCRSolicitudEtapaSeguimientoDTO>> getLcrSolicitudByNumOperacion(@PathVariable ("num_operacion") Long numOperacion) {
        try {
            if(numOperacion == null) return ResponseEntity.badRequest().body(new ApiResponseData<>("El numero de operacion es requerido", false));
            ApiResponseData<LCRSolicitudEtapaSeguimientoDTO> lcr = lcrSolicitudEtapaSeguimientoService.getLcrSolicitudByNumOperacion(numOperacion);
            return ResponseEntity.ok(lcr);
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponseData<>(e.getMessage(), false));
        }  
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody Map<String, ?> requestBody ) {
        try {
            Long lcrSolicitudId = requestBody.get("lcrSolicitudId") != null ? Long.parseLong(requestBody.get("lcrSolicitudId").toString()): null;
            Long usuarioId = requestBody.get("usuarioId") != null ? Long.parseLong(requestBody.get("usuarioId").toString()): null;
            Integer status = requestBody.get("status") != null ? Integer.parseInt(requestBody.get("status").toString()): null;
            String slug = requestBody.get("slug") != null ? requestBody.get("slug").toString(): null;
            
            ApiResponse apiResponse = lcrSolicitudEtapaSeguimientoService.update(lcrSolicitudId, usuarioId, status, slug);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/observar")
    public ResponseEntity<ApiResponse> observar(@RequestBody Map<String, ?> requestBody ) {
        try {
            Long lcrSolicitudId = requestBody.get("lcrSolicitudId") != null ? Long.parseLong(requestBody.get("lcrSolicitudId").toString()): null;
            Long usuarioId = requestBody.get("usuarioId") != null ? Long.parseLong(requestBody.get("usuarioId").toString()): null;
            String mensaje = requestBody.get("mensaje") != null ?  requestBody.get("mensaje").toString(): null;
            Integer type = requestBody.get("type") != null ? Integer.parseInt(requestBody.get("type").toString()): null;
            
            ApiResponse apiResponse = lcrSolicitudEtapaSeguimientoService.observar(lcrSolicitudId, usuarioId, mensaje, type);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/aprobar")
    public ResponseEntity<ApiResponse> aprobar(@RequestBody Map<String, ?> requestBody ) {
        try {
            Long lcrSolicitudId = requestBody.get("lcrSolicitudId") != null ? Long.parseLong(requestBody.get("lcrSolicitudId").toString()): null;
            Long usuarioId = requestBody.get("usuarioId") != null ? Long.parseLong(requestBody.get("usuarioId").toString()): null; 
            
            ApiResponse apiResponse = lcrSolicitudEtapaSeguimientoService.aprobar(lcrSolicitudId, usuarioId);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

}
