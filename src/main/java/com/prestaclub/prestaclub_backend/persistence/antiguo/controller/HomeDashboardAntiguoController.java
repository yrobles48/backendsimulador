package com.prestaclub.prestaclub_backend.persistence.antiguo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardListaTipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.DashboardTipoSolicitudDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.HomeDashboardAntiguoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/antiguo/home/dashboard")
@CrossOrigin("*")
public class HomeDashboardAntiguoController {

    @Autowired
    private HomeDashboardAntiguoService homeDashboardAntiguoService;

    @GetMapping("/tipo-solicitud")
    public ResponseEntity<ApiResponseData<List<DashboardTipoSolicitudDTO>>> getDashboardTipoSolicitud() {
        ApiResponseData<List<DashboardTipoSolicitudDTO>> apiResponseData = new ApiResponseData<>();
        try {
            List<DashboardTipoSolicitudDTO> dashboardTipoSolicitud = homeDashboardAntiguoService.getDashboardTipoSolicitud();
            apiResponseData.setData(dashboardTipoSolicitud);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }


    @PostMapping("/tipo-solicitud/lista")
    public ResponseEntity<ListaResponse> getDashboardListaTipoSolicitud(@RequestBody Map<String, String> requestBody) {
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber")) : 1;
        String filterText = requestBody.containsKey("filterText") ? requestBody.get("filterText") : "";
        Long tipoSolicitud = requestBody.get("tipoSolicitud") != null ? Long.parseLong(requestBody.get("tipoSolicitud")) : null;
        ListaResponse dashboardListaTipoSolicitud = homeDashboardAntiguoService.getDashboardListaTipoSolicitud(tipoSolicitud, pageSize, pageNumber, filterText);
        return ResponseEntity.ok(dashboardListaTipoSolicitud);
        // ApiResponseData<List<DashboardListaTipoSolicitudDTO>> apiResponseData = new ApiResponseData<>();
        // try {
        //     List<DashboardListaTipoSolicitudDTO> dashboardListaTipoSolicitudDTOs =  homeDashboardAntiguoService.getDashboardListaTipoSolicitud(tipoSolicitud);
        //     apiResponseData.setData(dashboardListaTipoSolicitudDTOs);
        //     apiResponseData.setSuccess(true);
        //     apiResponseData.setMessage("OK");
        //     return ResponseEntity.ok(apiResponseData);
        // } catch (Exception e) {
        //     apiResponseData.setSuccess(false);
        //     apiResponseData.setMessage(e.getMessage());
        //     return ResponseEntity.badRequest().body(apiResponseData);
        // }
    }
    
}
