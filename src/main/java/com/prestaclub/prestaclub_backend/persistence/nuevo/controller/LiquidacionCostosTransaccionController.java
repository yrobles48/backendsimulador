package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.GrabarLiquidacionRequest;
import com.prestaclub.prestaclub_backend.dto.LiquidacionCostosTransaccionResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRMontoEstadosDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosTransaccionService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/nuevo/liquidacion-costos-transaccion")
public class LiquidacionCostosTransaccionController {

    @Autowired
    LiquidacionCostosTransaccionService liquidacionCostosTransaccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> liquidacionCostosTransaccionesUltimo(
            @RequestBody Map<String, String> requestBody) {
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber"))
                : 1;
        Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
        ListaResponse liquidacionCostosTransaccionesLista = liquidacionCostosTransaccionService
                .liquidacionCostosTransaccionesLista(numOperacion, pageSize, pageNumber);
        return ResponseEntity.ok(liquidacionCostosTransaccionesLista);
    }

    @PostMapping("/ultimo-previo")
    public ResponseEntity<LiquidacionCostosTransaccionResponse> liquidacionCostosTransaccionesUltimoPrevio(
            @RequestBody Map<String, String> requestBody) {
        Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
        Integer liquidacionCostosTransaccionId = requestBody.get("liquidacionCostosTransaccionId") != null
                ? Integer.parseInt(requestBody.get("liquidacionCostosTransaccionId"))
                : null;
        LiquidacionCostosTransaccionResponse liquidacionCostosTransaccionesUltimoPrevio = liquidacionCostosTransaccionService
                .liquidacionCostosTransaccionesUltimoPrevio(numOperacion, liquidacionCostosTransaccionId);
        return ResponseEntity.ok(liquidacionCostosTransaccionesUltimoPrevio);
    }

    @PostMapping("/ultimo-previo-fecha")
    public ResponseEntity<List<LiquidacionCostosTransaccionResponse>> liquidacionCostosTransaccionesUltimoPrevioFecha(
            @RequestBody Map<String, String> requestBody) {
        String fechaini = requestBody.get("fechaini");
        String fechafin = requestBody.get("fechafin");
        String estado = requestBody.get("estado");
        List<LiquidacionCostosTransaccionResponse> l = liquidacionCostosTransaccionService
                .obtenerOperacionesXFecha(fechaini, fechafin, estado);
        return ResponseEntity.ok(l);
    }

    @PostMapping("/grabar-liquidacion")
    public ResponseEntity<ApiResponse> postMethodName(@RequestBody GrabarLiquidacionRequest entity) {
        Long numOperacion = entity.getNumOperacion();
        LiquidacionCostosTransaccion liquidacionCostosTransaccion = entity.getLiquidacionCostosTransaccion();
        List<LCTCronograma> lctCronograma = entity.getLctCronograma();
        ApiResponse apiResponse = liquidacionCostosTransaccionService.saveLiquidacionCostosTransaccion(numOperacion,
                liquidacionCostosTransaccion, lctCronograma);
        if (apiResponse.isSuccess()) {
            messagingTemplate.convertAndSend("/topic/cronograma/refresh", getPayloadSocketCronogramaRefresh(
                    numOperacion.intValue(), liquidacionCostosTransaccion.getCreatedBy(), 0));
            return ResponseEntity.ok(apiResponse);
        } else {
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    private Map<String, Object> getPayloadSocketCronogramaRefresh(Integer numOperacion, Long userId, Integer type) {
        UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById(userId);
        String message = "";
        Map<String, Object> payload = new HashMap<>();
        payload.put("usuario", usuario);
        payload.put("numOperacion", numOperacion);
        switch (type) {
            case 0:
                message = "El Usuario " + usuario.getUsername() + " grabó una liquidación de esta operación";
                break;
            case 1:
                message = "El Usuario " + usuario.getUsername() + " finalizó una liquidación de esta operación";
                break;
            case 2:
                message = "El Usuario " + usuario.getUsername() + " desembolsó una liquidación de esta operación";
                break;
            case 3:
                message = "El Usuario " + usuario.getUsername() + " reconsideró una liquidación de esta operación";
                break;
            case 4:
                message = "El Usuario " + usuario.getUsername() + " revirtió el desembolso de esta operación";
                break;
            default:
                break;
        }
        payload.put("message", message);
        return payload;
    }

    private Map<String, Object> getPayloadSocketSimulacionRefresh(Integer numOperacion, Long userId, Integer type,
            Integer liquidacionCostosTransaccionId) {
        UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById(userId);
        String message = "";
        Map<String, Object> payload = new HashMap<>();
        payload.put("usuario", usuario);
        payload.put("numOperacion", numOperacion);
        payload.put("lctId", liquidacionCostosTransaccionId);
        switch (type) {
            case 0:
                message = "El Usuario " + usuario.getUsername() + " cambió a estado Final la operación " + numOperacion;
                break;
            case 1:
                message = "El Usuario " + usuario.getUsername() + " cambió a estado Desembolsado la operación "
                        + numOperacion;
                break;
            case 2:
                message = "El Usuario " + usuario.getUsername() + " reconsideró la operación " + numOperacion;
                break;
            case 3:
                message = "El Usuario " + usuario.getUsername() + " revirtió el desembolso de la operación "
                        + numOperacion;
            default:
                break;
        }
        payload.put("message", message);
        return payload;
    }

    @PostMapping("/update/final")
    public ResponseEntity<ApiResponse> updateFinalLiquidacion(@RequestBody Map<String, String> requestBody) {
        Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
        Integer liquidacionCostosTransaccionId = requestBody.get("liquidacionCostosTransaccionId") != null
                ? Integer.parseInt(requestBody.get("liquidacionCostosTransaccionId"))
                : null;
        ApiResponse resp = liquidacionCostosTransaccionService.updateFinalLiquidacion(numOperacion,
                liquidacionCostosTransaccionId);
        if (resp.isSuccess()) {
            messagingTemplate.convertAndSend("/topic/cronograma/refresh",
                    getPayloadSocketCronogramaRefresh(numOperacion, Long.parseLong(requestBody.get("userId")), 1));
            messagingTemplate.convertAndSend("/topic/simulador/refresh", getPayloadSocketSimulacionRefresh(numOperacion,
                    Long.parseLong(requestBody.get("userId")), 0, liquidacionCostosTransaccionId));
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping("/update/desembolso")
    public ResponseEntity<ApiResponse> updateDesembolsoLiquidacion(@RequestBody Map<String, String> requestBody) {
        try {
            Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
            Integer liquidacionCostosTransaccionId = requestBody.get("liquidacionCostosTransaccionId") != null
                    ? Integer.parseInt(requestBody.get("liquidacionCostosTransaccionId"))
                    : null;
            BigDecimal tasaMoratoria = requestBody.get("tasa_moratoria") != null
                    ? new BigDecimal(requestBody.get("tasa_moratoria"))
                    : null;
            ApiResponse resp = liquidacionCostosTransaccionService.updateDesembolsoLiquidacion(numOperacion,
                    tasaMoratoria, liquidacionCostosTransaccionId, Long.parseLong(requestBody.get("userId")));
            if (resp.isSuccess()) {
                messagingTemplate.convertAndSend("/topic/cronograma/refresh",
                        getPayloadSocketCronogramaRefresh(numOperacion, Long.parseLong(requestBody.get("userId")), 2));
                messagingTemplate.convertAndSend("/topic/simulador/refresh", getPayloadSocketSimulacionRefresh(
                        numOperacion, Long.parseLong(requestBody.get("userId")), 1, liquidacionCostosTransaccionId));

                Map<String, Object> payload = getPayloadSocketSimulacionRefresh(numOperacion,
                        Long.parseLong(requestBody.get("userId")), 1, liquidacionCostosTransaccionId);
                payload.put("slug", "desembolso");

                messagingTemplate.convertAndSend("/topic/liquidacion/refresh", payload);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.badRequest().body(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/update/revertir-desembolso")
    public ResponseEntity<ApiResponse> updateRevertirDesembolsoLiquidacion(
            @RequestBody Map<String, String> requestBody) {
        try {
            Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
            Integer liquidacionCostosTransaccionId = requestBody.get("liquidacionCostosTransaccionId") != null
                    ? Integer.parseInt(requestBody.get("liquidacionCostosTransaccionId"))
                    : null;
            ApiResponse resp = liquidacionCostosTransaccionService.updateRevertirDesembolsoLiquidacion(numOperacion,
                    liquidacionCostosTransaccionId);
            if (resp.isSuccess()) {
                messagingTemplate.convertAndSend("/topic/cronograma/refresh",
                        getPayloadSocketCronogramaRefresh(numOperacion, Long.parseLong(requestBody.get("userId")), 4));
                messagingTemplate.convertAndSend("/topic/simulador/refresh", getPayloadSocketSimulacionRefresh(
                        numOperacion, Long.parseLong(requestBody.get("userId")), 3, liquidacionCostosTransaccionId));
                Map<String, Object> payload = getPayloadSocketSimulacionRefresh(numOperacion,
                        Long.parseLong(requestBody.get("userId")), 3, liquidacionCostosTransaccionId);
                payload.put("slug", "revertir-desembolso");
                messagingTemplate.convertAndSend("/topic/liquidacion/refresh", payload);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.badRequest().body(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/update/reconsiderar")
    public ResponseEntity<ApiResponse> reconsiderarLiquidacion(@RequestBody Map<String, String> requestBody) {
        Integer numOperacion = Integer.parseInt(requestBody.get("numOperacion"));
        Integer liquidacionCostosTransaccionId = requestBody.get("liquidacionCostosTransaccionId") != null
                ? Integer.parseInt(requestBody.get("liquidacionCostosTransaccionId"))
                : null;
        ApiResponse resp = liquidacionCostosTransaccionService.reconsiderarLiquidacion(numOperacion);
        if (resp.isSuccess()) {
            messagingTemplate.convertAndSend("/topic/cronograma/refresh",
                    getPayloadSocketCronogramaRefresh(numOperacion, Long.parseLong(requestBody.get("userId")), 3));
            messagingTemplate.convertAndSend("/topic/simulador/refresh", getPayloadSocketSimulacionRefresh(numOperacion,
                    Long.parseLong(requestBody.get("userId")), 2, liquidacionCostosTransaccionId));
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping("/final/lista")
    public ResponseEntity<ListaResponse> listaFinalLiquidaciones(@RequestBody Map<String, String> requestBody) {
        ListaResponse response = new ListaResponse();
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber"))
                : 1;
        String filterText = requestBody.containsKey("filterText") ? requestBody.get("filterText") : "";
        try {
            response = liquidacionCostosTransaccionService.listaFinalLiquidaciones(pageSize, pageNumber, filterText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/desembolso/lista")
    public ResponseEntity<ListaResponse> listaDesembolsoLiquidaciones(@RequestBody Map<String, String> requestBody) {
        ListaResponse response = new ListaResponse();
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber"))
                : 1;
        String filterText = requestBody.containsKey("filterText") ? requestBody.get("filterText") : "";
        Boolean filterPending = requestBody.containsKey("filterPending")
                ? Boolean.parseBoolean(requestBody.get("filterPending"))
                : false;
        Boolean filterApproved = requestBody.containsKey("filterApproved")
                ? Boolean.parseBoolean(requestBody.get("filterApproved"))
                : false;
        Boolean filterObservado = requestBody.containsKey("filterObservado")
                ? Boolean.parseBoolean(requestBody.get("filterObservado"))
                : false;
        String slug = requestBody.containsKey("slug") ? requestBody.get("slug") : "";
        try {
            response = liquidacionCostosTransaccionService.listaDesembolsoLiquidaciones(pageSize, pageNumber,
                    filterText, slug, filterPending, filterApproved, filterObservado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/final/counter")
    public ResponseEntity<ApiResponseData<Integer>> counterFinalLiquidaciones() {
        ApiResponseData<Integer> response = new ApiResponseData<>();
        try {
            Integer count = liquidacionCostosTransaccionService.countLctFinal();
            Map<String, Object> payload = new HashMap<>();
            payload.put("moduleId", 3);
            payload.put("count", count);
            payload.put("slug", "simulacion");
            messagingTemplate.convertAndSend("/topic/counter/count-refresh", payload);
            response.setData(count);
            response.setSuccess(true);
            response.setMessage("OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/desembolso/counter")
    public ResponseEntity<ApiResponseData<Integer>> counterDesembolsoLiquidaciones() {
        ApiResponseData<Integer> response = new ApiResponseData<>();
        try {
            Integer count = liquidacionCostosTransaccionService.countLctDesembolso();
            Map<String, Object> payload = new HashMap<>();
            payload.put("moduleId", 3);
            payload.put("count", count);
            payload.put("slug", "liquidacion-costos");
            messagingTemplate.convertAndSend("/topic/counter/count-refresh", payload);
            response.setData(count);
            response.setSuccess(true);
            response.setMessage("OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/desembolso/counter/2")
    public ResponseEntity<ApiResponseData<Integer>> counterDesembolsoLiquidaciones2() {
        ApiResponseData<Integer> response = new ApiResponseData<>();
        try {
            Integer count = liquidacionCostosTransaccionService.countLctDesembolso();
            Map<String, Object> payload = new HashMap<>();
            payload.put("moduleId", 2);
            payload.put("count", count);
            payload.put("slug", "liquidacion-costos");
            messagingTemplate.convertAndSend("/topic/counter/count-refresh", payload);
            response.setData(count);
            response.setSuccess(true);
            response.setMessage("OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/grabar-borrador")
    public ResponseEntity<ApiResponse> grabarBorrador(@RequestBody GrabarLiquidacionRequest entity) {
        Long numOperacion = entity.getNumOperacion();
        LiquidacionCostosTransaccion liquidacionCostosTransaccion = entity.getLiquidacionCostosTransaccion();
        List<LCTCronograma> lctCronograma = entity.getLctCronograma();
        Long userId = entity.getUserId();
        ApiResponse apiResponse = liquidacionCostosTransaccionService.guardarBorrador(liquidacionCostosTransaccion,
                lctCronograma, userId);
        if (apiResponse.isSuccess()) {
            messagingTemplate.convertAndSend("/topic/cronograma/refresh",
                    getPayloadSocketCronogramaRefresh(numOperacion.intValue(), userId, 0));
            return ResponseEntity.ok(apiResponse);
        } else {
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/descargar-reporte-desembolso")
    public ResponseEntity<byte[]> descargarReporteDesembolso(@RequestBody Map<String, ?> requestBody) {
        try {
            // capturar startDate
            String startDate = requestBody.get("startDate") != null ? requestBody.get("startDate").toString() : null;
            // capturar endDate
            String endDate = requestBody.get("endDate") != null ? requestBody.get("endDate").toString() : null;

            byte[] excelBytes = liquidacionCostosTransaccionService.descargarReporteDesembolso(startDate, endDate);

            if (excelBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Tipo de archivo genérico
            headers.setContentDispositionFormData("attachment", "simulacion_desembolso.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/descargar-reporte-saldos")
    public ResponseEntity<byte[]> descargarReporteSaldos(@RequestBody Map<String, ?> requestBody) {
        try {

            String fechaini = requestBody.get("startDate") != null ? requestBody.get("startDate").toString() : null;
            String fechafin = requestBody.get("endDate") != null ? requestBody.get("endDate").toString() : null;
            String estado = requestBody.get("estado") != null ? requestBody.get("estado").toString() : null;

            byte[] excelBytes = liquidacionCostosTransaccionService.descargarReporteSaldos(fechaini, fechafin, estado);

            if (excelBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Tipo de archivo genérico
            headers.setContentDispositionFormData("attachment", "simulacion_desembolso.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/desembolso/montos-lcr-solicitud")
    public ResponseEntity<ApiResponseData<LCRMontoEstadosDTO>> getMontoEstados(
            @RequestBody Map<String, ?> requestBody) {
        ApiResponseData<LCRMontoEstadosDTO> apiResponseData = new ApiResponseData<>();
        try {
            String slug = requestBody.get("slug") != null ? requestBody.get("slug").toString() : "";
            LCRMontoEstadosDTO montoEstados = liquidacionCostosTransaccionService.getMontoEstados(slug);
            apiResponseData.setData(montoEstados);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponseData<LCRMontoEstadosDTO>(null, e.getMessage(), false));
        }
    }

}
