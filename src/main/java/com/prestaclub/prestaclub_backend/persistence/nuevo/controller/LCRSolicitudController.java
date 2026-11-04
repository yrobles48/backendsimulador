package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudEtapaSeguimientoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

@RestController
@RequestMapping("/nuevo/lcr-solicitud")
public class LCRSolicitudController {

    @Autowired
    private LCRSolicitudService lcrSolicitudService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LCRSolicitudEtapaSeguimientoService lcrSolicitudEtapaSeguimientoService;

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> listaLcr(@RequestBody Map<String, ?> requestBody) {
        try {
            Integer pageSize = requestBody.get("pageSize") != null
                    ? Integer.parseInt(requestBody.get("pageSize").toString())
                    : 2;
            Integer pageNumber = requestBody.get("pageNumber") != null
                    ? Integer.parseInt(requestBody.get("pageNumber").toString())
                    : 1;
            String filterText = requestBody.get("filterText") != null ? requestBody.get("filterText").toString() : "";
            // status}
            Integer status = requestBody.get("status") != null ? Integer.parseInt(requestBody.get("status").toString())
                    : null;
            ListaResponse lcr = lcrSolicitudService.listaLcrSolicitud(pageSize, pageNumber, filterText, status);
            return ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/actualizar-estado")
    public ResponseEntity<ApiResponse> actualizarEstado(@RequestBody Aprobados requestBody) {
        try {

            String ids[] = requestBody.getIds().split(",");
            for (String id : ids) {
                Long lcrSolicitudId = Long.parseLong(id);
                Long usuarioId = requestBody.getUsuarioId() != null ? requestBody.getUsuarioId() : null;

                // lcrSolicitudService.actualizarEstadoAprobado(Long.parseLong(id));
                ApiResponse apiResponse = lcrSolicitudEtapaSeguimientoService.aprobar(lcrSolicitudId, usuarioId);
            }
            ApiResponse apiResponse2 = new ApiResponse();
            apiResponse2.setSuccess(true);
            apiResponse2.setMessage("Estados actualizados correctamente");
            return ResponseEntity.ok(apiResponse2);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // @PostMapping("/aprobar")
    // public ResponseEntity<ApiResponse> aprobar(@RequestBody Map<String, ?>
    // requestBody) {
    // Long userId = requestBody.get("userId") != null ?
    // Long.parseLong(requestBody.get("userId").toString()): null;
    // Long numOperacion = requestBody.get("numOperacion") != null ?
    // Long.parseLong(requestBody.get("numOperacion").toString()): null;
    // try {
    // ApiResponse apiResponse = lcrSolicitudService.aprobar(numOperacion, userId);
    // return ResponseEntity.ok(apiResponse);
    // } catch (Exception e) {
    // ApiResponse apiResponse = new ApiResponse();
    // apiResponse.setSuccess(false);
    // apiResponse.setMessage("Error al guardar " + e.getMessage());
    // return ResponseEntity.badRequest().body(apiResponse);
    // }
    // }

    // @PostMapping("/observar")
    // public ResponseEntity<ApiResponse> observar(@RequestBody Map<String, ?>
    // requestBody) {
    // Long userId = requestBody.get("userId") != null ?
    // Long.parseLong(requestBody.get("userId").toString()): null;
    // Long numOperacion = requestBody.get("numOperacion") != null ?
    // Long.parseLong(requestBody.get("numOperacion").toString()): null;
    // String observacion = requestBody.get("observacion") != null ?
    // requestBody.get("observacion").toString(): null;
    // try {
    // ApiResponse apiResponse = lcrSolicitudService.observar(numOperacion, userId,
    // observacion);
    // if (apiResponse.isSuccess()) {
    // UsuarioMantenimientoDTO usuario =
    // usuarioService.obtenerUsuarioMantenimientoById(userId);
    // Map<String, Object> payload = new HashMap<>();
    // payload.put("numOperacion", numOperacion);
    // payload.put("usuario", usuario);
    // payload.put("message", "El usuario " + usuario.getNombreCompleto() + " ha
    // observado la solicitud de la liquidacion de la operacion " + numOperacion);
    // payload.put("slug", "observar");
    // messagingTemplate.convertAndSend("/topic/legal/solicitud-aprobacion",
    // payload);
    // messagingTemplate.convertAndSend("/topic/liquidacion/refresh", payload);
    // payload.put("statusPrevio", 1);
    // payload.put("statusActual", 2);
    // messagingTemplate.convertAndSend("/topic/contabilidad/lcr-solicitud/refresh",
    // payload);
    // }
    // return ResponseEntity.ok(apiResponse);
    // } catch (Exception e) {
    // ApiResponse apiResponse = new ApiResponse();
    // apiResponse.setSuccess(false);
    // apiResponse.setMessage("Error al guardar " + e.getMessage());
    // return ResponseEntity.badRequest().body(apiResponse);
    // }
    // }

    @GetMapping("/pendiente/counter")
    public ResponseEntity<ApiResponseData<Integer>> pendingCounter() {
        ApiResponseData<Integer> response = new ApiResponseData<>();
        try {
            Integer count = lcrSolicitudService.countLcrSolicitudPendiente();
            Map<String, Object> payload = new HashMap<>();
            payload.put("moduleId", 5);
            payload.put("count", count);
            payload.put("slug", "lcr-solicitud");
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

    @PostMapping("/descargar-reporte-aprobados")
    public ResponseEntity<byte[]> descargarReporteAprobados(@RequestBody Map<String, ?> requestBody) {
        try {
            // capturar startDate
            String startDate = requestBody.get("startDate") != null ? requestBody.get("startDate").toString() : null;
            // capturar endDate
            String endDate = requestBody.get("endDate") != null ? requestBody.get("endDate").toString() : null;

            String numOperacion = requestBody.get("numOperacion") != null ? requestBody.get("numOperacion").toString()
                    : null;
            String estado = requestBody.get("estado") != null ? requestBody.get("estado").toString()
                    : null;
            System.out.println("numoperacion"+numOperacion);
            byte[] excelBytes = lcrSolicitudService.descargarReporteAprobados(startDate, endDate, numOperacion, estado);

            if (excelBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Tipo de archivo genÃ©rico
            headers.setContentDispositionFormData("attachment", "liquidaciones_aprobadas.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    /////////////////////////////
    @GetMapping("/{num_operacion}")
    public ResponseEntity<ApiResponseData<List<LCRSolicitudEtapaSeguimiento>>> getLcrSolicitudByNumOperacion(
            @PathVariable("num_operacion") Long numOperacion) {
        try {
            if (numOperacion == null)
                return ResponseEntity.badRequest()
                        .body(new ApiResponseData<>("El numero de operacion es requerido", false));
            ApiResponseData<List<LCRSolicitudEtapaSeguimiento>> lcr = lcrSolicitudService
                    .getLcrSolicitudByNumOperacion(numOperacion);
            return ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponseData<>(e.getMessage(), false));
        }
    }

    @PostMapping("/historial")
    public ResponseEntity<ListaResponse> historial(@RequestBody Map<String, ?> requestBody) {
        Integer pageSize = requestBody.get("pageSize") != null
                ? Integer.parseInt(requestBody.get("pageSize").toString())
                : 2;
        Integer pageNumber = requestBody.get("pageNumber") != null
                ? Integer.parseInt(requestBody.get("pageNumber").toString())
                : 1;
        String filterText = requestBody.get("filterText") != null ? requestBody.get("filterText").toString() : "";
        Long numOperacion = requestBody.get("numOperacion") != null
                ? Long.parseLong(requestBody.get("numOperacion").toString())
                : null;
        try {

            ListaResponse lcr = lcrSolicitudService.historialLcrSolicitudByNumOperacion(pageSize, pageNumber,
                    filterText, numOperacion);
            return ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            ListaResponse lcr = new ListaResponse();
            lcr.setSuccess(false);
            lcr.setMessage(e.getMessage());
            lcr.setData(null);
            lcr.setPageNumber(pageNumber);
            lcr.setPageSize(pageSize);
            lcr.setTotal(0);
            return ResponseEntity.ok(lcr);

        }
    }

    @PostMapping("/descargar-reporte-devoluciones-resoluciones")
    public ResponseEntity<byte[]> descargarReporteDevolucionesResoluciones(@RequestBody Map<String, ?> requestBody) {
        try {
            // capturar startDate
            String startDate = requestBody.get("startDate") != null ? requestBody.get("startDate").toString() : null;
            // capturar endDate
            String endDate = requestBody.get("endDate") != null ? requestBody.get("endDate").toString() : null;

            String tchn = requestBody.get("tchn") != null ? requestBody.get("tchn").toString()
                    : null;

            Integer estado = requestBody.get("estado") != "0" ? Integer.valueOf(requestBody.get("estado").toString())
                    : 0;
            System.out.println("estado" + estado);
            byte[] excelBytes = lcrSolicitudService.descargarReporteDevolucionesResoluciones(startDate, endDate, tchn,
                    estado);

            if (excelBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Tipo de archivo genÃ©rico
            headers.setContentDispositionFormData("attachment", "liquidaciones_aprobadas.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

}

class Aprobados {
    Long usuarioId;
    String ids;

    public String getIds() {
        return ids;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}