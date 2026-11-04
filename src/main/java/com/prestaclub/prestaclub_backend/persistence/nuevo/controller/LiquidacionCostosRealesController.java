package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.LcrResponse;
import com.prestaclub.prestaclub_backend.dto.LcrTotalResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosRealesService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LogErrorService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/nuevo/liquidacion-costos-reales")
public class LiquidacionCostosRealesController {
    @Autowired
    private LiquidacionCostosRealesService liquidacionCostosRealesService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private LogErrorService logErrorService;
    @PostMapping("/subida-masiva")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId, @RequestParam("moduloId") Long moduloId) {
        ApiResponse apiResponse = new ApiResponse();
        if (file.isEmpty()) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("El archivo esta vacio");
            return ResponseEntity.badRequest().body(apiResponse);
        }
        try {
            apiResponse = liquidacionCostosRealesService.cargaMasiva(file, userId, moduloId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            logErrorService.guardar(e, "nuevo/liquidacion-costos-reales/subida-masiva");
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al subir archivo " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> listaLcr(@RequestBody Map<String, ?> requestBody) { 
        try {
            Integer pageSize = requestBody.get("pageSize") != null ? Integer.parseInt(requestBody.get("pageSize").toString()): 2;
            Integer pageNumber = requestBody.get("pageNumber") != null ? Integer.parseInt(requestBody.get("pageNumber").toString()): 1;
            String filterText = requestBody.get("filterText") != null ? requestBody.get("filterText").toString(): "";
            Long numOperacion = requestBody.get("numOperacion") != null ? Long.parseLong(requestBody.get("numOperacion").toString()): null;
            
            ListaResponse lcr = liquidacionCostosRealesService.listaLcrByNumOperacion(numOperacion, pageSize, pageNumber, filterText);  
            return ResponseEntity.ok(lcr);
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/lista/all")
    public ResponseEntity<ApiResponseData<List<LcrTotalResponse>>> listaLcrAll(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<List<LcrTotalResponse>> apiResponseData = new ApiResponseData<>();
        try {
            Long numOperacion = requestBody.get("numOperacion") != null ? Long.parseLong(requestBody.get("numOperacion").toString()): null;
            Boolean esDolares = requestBody.get("esDolares") != null ? Boolean.parseBoolean(requestBody.get("esDolares").toString()): null;
            List<LcrTotalResponse> lcr = liquidacionCostosRealesService.listaLcrAllByNumOperacion(numOperacion, esDolares);  
            apiResponseData.setData(lcr); 
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK"); 
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) { 
            e.printStackTrace();
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody Map<String, ?> requestBody) {
        Long idLCRConceptoServicio = requestBody.get("idLCRConceptoServicio") != null ? Long.parseLong(requestBody.get("idLCRConceptoServicio").toString()): null;
        Long idLCRServicio = requestBody.get("idLCRServicio") != null ? Long.parseLong(requestBody.get("idLCRServicio").toString()): null;
        String referencia = requestBody.get("referencia") != null ? requestBody.get("referencia").toString(): null;
        Long kardex = requestBody.get("kardex") != null ? Long.parseLong(requestBody.get("kardex").toString()): null;
        BigDecimal valor = requestBody.get("valor") != null ? new BigDecimal(requestBody.get("valor").toString()): null;
        Boolean esDolares = requestBody.get("esDolares") != null ? Boolean.parseBoolean(requestBody.get("esDolares").toString()): null;
        Long numOperacion = requestBody.get("numOperacion") != null ? Long.parseLong(requestBody.get("numOperacion").toString()): null;
        BigDecimal tipoCambio = requestBody.get("tipoCambio") != null ? new BigDecimal(requestBody.get("tipoCambio").toString()): null;
        Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null; 

        try {
            ApiResponse apiResponse = liquidacionCostosRealesService.addLcr( numOperacion ,idLCRConceptoServicio, idLCRServicio, referencia, kardex, valor, esDolares, tipoCambio, userId);
            if (apiResponse.isSuccess()) {
                UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById(userId);
                Map<String, Object> payload = new HashMap<>();
                payload.put("numOperacion", numOperacion);
                payload.put("usuario", usuario);    
                payload.put("message", "El usuario " + usuario.getNombreCompleto() + " ha agregado un nuevo Costo Real");
                messagingTemplate.convertAndSend("/topic/legal/seguimiento-costos",  payload); 
            }
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody Map<String, ?> requestBody) { 
        Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null;
        String referencia = requestBody.get("referencia") != null ? requestBody.get("referencia").toString(): null;
        Long kardex = requestBody.get("kardex") != null ? Long.parseLong(requestBody.get("kardex").toString()): null;
        BigDecimal valor = requestBody.get("valor") != null ? new BigDecimal(requestBody.get("valor").toString()): null;
        Boolean esDolares = requestBody.get("esDolares") != null ? Boolean.parseBoolean(requestBody.get("esDolares").toString()): null; 
        BigDecimal tipoCambio = requestBody.get("tipoCambio") != null ? new BigDecimal(requestBody.get("tipoCambio").toString()): null;
        Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null; 

        try {
            ApiResponse apiResponse = liquidacionCostosRealesService.updateLcr(id, referencia, kardex, valor, esDolares, tipoCambio, userId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@RequestBody Map<String, ?> requestBody) { 
        Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null;
        Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;
        try {
            ApiResponse apiResponse = liquidacionCostosRealesService.deleteLcr(id, userId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al eliminar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<ApiResponseData<LcrResponse>> buscar(@RequestBody Map<String, ?> requestBody) { 
        ApiResponseData<LcrResponse> apiResponseData = new ApiResponseData<>();
        try {
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null;
            LcrResponse lcr = liquidacionCostosRealesService.buscarLcr(id);
            apiResponseData.setData(lcr);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");

            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) { 
            e.printStackTrace();
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }

    // @PostMapping("/solicitud-aprobacion")
    // public ResponseEntity<ApiResponse> solicitudAprobacion(@RequestBody Map<String, ?> requestBody) { 
    //     BigDecimal montoSimulado = requestBody.get("montoSimulado") != null ? new BigDecimal(requestBody.get("montoSimulado").toString()): null;
    //     BigDecimal montoReal = requestBody.get("montoReal") != null ? new BigDecimal(requestBody.get("montoReal").toString()): null;
    //     Long numOperacion = requestBody.get("numOperacion") != null ? Long.parseLong(requestBody.get("numOperacion").toString()): null;
    //     Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;
    //     Boolean esDolares = requestBody.get("esDolares") != null ? Boolean.parseBoolean(requestBody.get("esDolares").toString()): null;
    //     String cuentaBancaria = requestBody.get("cuentaBancaria") != null ? requestBody.get("cuentaBancaria").toString(): null;
    //     String cci = requestBody.get("cci") != null ? requestBody.get("cci").toString(): null;
    //     try {
    //         ApiResponse apiResponse = liquidacionCostosRealesService.solicitarAprobacion(montoSimulado, montoReal, numOperacion, userId, esDolares, cuentaBancaria, cci); 
    //         if (apiResponse.isSuccess()) {
    //             UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById(userId);
    //             Map<String, Object> payload = new HashMap<>();
    //             payload.put("numOperacion", numOperacion);
    //             payload.put("usuario", usuario);
    //             payload.put("message", "El usuario " + usuario.getNombreCompleto() + " ha solicitado la aprobación de la liquidación de costos de la operación " + numOperacion);
    //             payload.put("slug", "solicitud-aprobacion");
    //             messagingTemplate.convertAndSend("/topic/legal/solicitud-aprobacion",  payload); 
    //             messagingTemplate.convertAndSend("/topic/liquidacion/refresh",  payload);  
    //             payload.put("statusActual", 1);
    //             messagingTemplate.convertAndSend("/topic/contabilidad/lcr-solicitud/refresh",  payload); 
    //         }
    //         return ResponseEntity.ok(apiResponse);
    //     } catch (Exception e) {
    //         ApiResponse apiResponse = new ApiResponse();
    //         apiResponse.setSuccess(false);
    //         apiResponse.setMessage("Error al guardar " + e.getMessage());
    //         return ResponseEntity.badRequest().body(apiResponse);
    //     }
    // }

    // @PostMapping("/reenviar-solicitud")
    // public ResponseEntity<ApiResponse> reenviarSolicitud(@RequestBody Map<String, ?> requestBody) { 
    //     //id
    //     Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null; 
    //     BigDecimal montoReal = requestBody.get("montoReal") != null ? new BigDecimal(requestBody.get("montoReal").toString()): null; 
    //     Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;   
    //     Long numOperacion = requestBody.get("numOperacion") != null ? Long.parseLong(requestBody.get("numOperacion").toString()): null;
    //     try {
    //         ApiResponse apiResponse = liquidacionCostosRealesService.reenviarSolicitud(id, montoReal, userId);
    //         if (apiResponse.isSuccess()) {
    //             UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById(userId);
    //             Map<String, Object> payload = new HashMap<>();
    //             payload.put("numOperacion", numOperacion);
    //             payload.put("usuario", usuario);
    //             payload.put("message", "El usuario " + usuario.getNombreCompleto() + " ha reenviado la solicitud de la liquidación de costos de la operación " + numOperacion);
    //             payload.put("slug", "reenviar-solicitud");
    //             messagingTemplate.convertAndSend("/topic/legal/solicitud-aprobacion",  payload); 
    //             messagingTemplate.convertAndSend("/topic/liquidacion/refresh",  payload);  
    //             payload.put("statusPrevio", 2);
    //             payload.put("statusActual", 1);
    //             messagingTemplate.convertAndSend("/topic/contabilidad/lcr-solicitud/refresh",  payload); 
    //         }
    //         return ResponseEntity.ok(apiResponse);
    //     } catch (Exception e) {
    //         ApiResponse apiResponse = new ApiResponse();
    //         apiResponse.setSuccess(false);
    //         apiResponse.setMessage("Error al guardar " + e.getMessage());
    //         return ResponseEntity.badRequest().body(apiResponse);
    //     }
    // }
    
}
