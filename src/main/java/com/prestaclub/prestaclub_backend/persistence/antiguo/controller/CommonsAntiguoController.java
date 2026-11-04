package com.prestaclub.prestaclub_backend.persistence.antiguo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.TipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.CommonsAntiguoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/antiguo/commons")
public class CommonsAntiguoController {

    @Autowired
    CommonsAntiguoService commonsAntiguoService;
    @GetMapping("/tipo-solicitudes")
    public ResponseEntity<ApiResponseData<List<TipoSolicitudDTO>>> getTipoSolicitudes() {
        ApiResponseData<List<TipoSolicitudDTO>> apiResponseData = new ApiResponseData<>();
        try {
            List<TipoSolicitudDTO> tipoSolicitudes = commonsAntiguoService.getTipoSolicitudes();
            apiResponseData.setData(tipoSolicitudes);
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
