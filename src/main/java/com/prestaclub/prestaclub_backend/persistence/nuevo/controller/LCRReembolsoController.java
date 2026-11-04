package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.RptMensaje;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRReembolsoService;

@RestController
@RequestMapping("/nuevo/lcr-reembolso")
public class LCRReembolsoController {

    @Autowired
    private LCRReembolsoService lcrReembolsoService;

    // ...existing code...
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
            Long status = requestBody.get("status") != null ? Long.parseLong(requestBody.get("status").toString())
                    : null;
            // status}
            ListaResponse lcrC = lcrReembolsoService.listaContabilidad();
            ListaResponse lcr = lcrReembolsoService.listaLcrReembolso(pageSize, pageNumber, filterText, status);

            // Obtener la lista de numOperacion de lcrC para comparar
            List<Long> numOperacionesC = lcrC.getData().stream()
                    .map(item -> ((LCRReembolsoDTO) item).getLcrReembolso().getNumOperacion())
                    .collect(Collectors.toList());

            // Filtrar los elementos de lcr que no estén en numOperacionesC
            List<Object> filteredData = lcr.getData().stream()
                    .filter(item -> {
                        Long numOp = ((LCRReembolsoDTO) item).getLcrReembolso().getNumOperacion();
                        return !numOperacionesC.contains(numOp);
                    })
                    .collect(Collectors.toList());

            lcr.setData(filteredData);
            // Actualizar el total si existe
            lcr.setTotal(lcr.getTotal());

            return ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    // ...existing code...

    @PostMapping("/listaAprobarTodos")
    public ResponseEntity<ListaResponse> listaLcrAprobarTodos(@RequestBody Map<String, ?> requestBody) {
        try {
            Integer pageSize = requestBody.get("pageSize") != null
                    ? Integer.parseInt(requestBody.get("pageSize").toString())
                    : 2;
            Integer pageNumber = requestBody.get("pageNumber") != null
                    ? Integer.parseInt(requestBody.get("pageNumber").toString())
                    : 1;
            String filterText = requestBody.get("filterText") != null ? requestBody.get("filterText").toString() : "";
            Long status = requestBody.get("status") != null ? Long.parseLong(requestBody.get("status").toString())
                    : null;
            // status}

            ListaResponse lcr = lcrReembolsoService.listaLcrReembolso(pageSize, pageNumber, filterText, status);
            // aprobarPendienteTodos(lcr);
            ApiResponse apiResponse = lcrReembolsoService.aprobarPendienteTodos(lcr);
            return null;// ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/actestado")
    public ResponseEntity<ApiResponse> actualizarestado(@RequestBody Map<String, ?> requestBody) {
        try {
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            Long lsestado = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : 0;
            lcrReembolsoService.actualizaReembolso(nroOperacion, lsestado);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/actfaplica")
    public ResponseEntity<ApiResponse> actualizarfaplica(@RequestBody Map<String, ?> requestBody) {
        try {
            Long nroOperacion = requestBody.get("num_operacion") != null
                    ? Long.parseLong(requestBody.get("num_operacion").toString())
                    : null;
            Long userID = requestBody.get("modify_by") != null ? Long.parseLong(requestBody.get("modify_by").toString())
                    : 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato si es necesario
            Object faplicacionObj = requestBody.get("faplicacion");
            Date fecha = null;
            if (faplicacionObj != null) {
                try {
                    fecha = sdf.parse(faplicacionObj.toString());
                } catch (ParseException e) {
                    // Manejo de error si la fecha es inválida
                    e.printStackTrace();
                }
            }


            lcrReembolsoService.actualizafaplicacion(nroOperacion, fecha);

            String titulo = "se envio información";
            String contenido = "Se aplica el codigo " + nroOperacion;
            String subject = "se aplico el codigo N° operación:" + nroOperacion;
            lcrReembolsoService.enviaContabilidad(nroOperacion, List.of("contabilidad"), subject, titulo, contenido,
                    2L, userID);

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/aprobar-pendiente")
    public ResponseEntity<ApiResponse> aprobarPendiente(@RequestBody Map<String, ?> requestBody) {
        try {
            Long lcrReembolsoId = requestBody.get("lcrReembolsoId") != null
                    ? Long.parseLong(requestBody.get("lcrReembolsoId").toString())
                    : null;
            Long usuarioId = requestBody.get("usuarioId") != null
                    ? Long.parseLong(requestBody.get("usuarioId").toString())
                    : null;
            ApiResponse apiResponse = lcrReembolsoService.aprobarPendiente(lcrReembolsoId, usuarioId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    // @PostMapping("/aprobar-pendiente-todos")
    public ResponseEntity<ApiResponse> aprobarPendienteTodos(ListaResponse requestBody) {
        try {
            // Long lcrReembolsoId = requestBody.get("lcrReembolsoId") != null ?
            // Long.parseLong(requestBody.get("lcrReembolsoId").toString()): null;
            // Long usuarioId = requestBody.get("usuarioId") != null ?
            // Long.parseLong(requestBody.get("usuarioId").toString()): null;
            // ApiResponse apiResponse =
            // lcrReembolsoService.aprobarPendienteTodos(lcrReembolsoId, usuarioId);
            // return ResponseEntity.ok(apiResponse);
            return null;
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al actualizar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @GetMapping("/listacontabilidad")
    public ResponseEntity<ListaResponse> listaContabilidad() {
        try {
            ListaResponse lcr = lcrReembolsoService.listaContabilidad();
            return ResponseEntity.ok(lcr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/totcontabilidad")
    public Integer totalContabilidad() {
        try {
            Integer tot = lcrReembolsoService.totalContabilidad();
            return tot;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping("/enviarcontabilidad")
    public ResponseEntity<ApiResponse> consultarenviar(@RequestBody Map<String, ?> requestBody) {
        try {
            Long lsestado = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : null;

            Object listaObj = requestBody.get("lista") != null ? requestBody.get("lista") : null;

            System.out.println("consultarenviar" + lsestado);
            Long nroOperacion = 0L;
            if (listaObj == null) {
                nroOperacion = requestBody.get("nroOperacion") != null
                        ? Long.parseLong(requestBody.get("nroOperacion").toString())
                        : null;
                String titulo = "se envio información";
                String contenido = "Se deriva a contalibilidad la devolución del codigo " + nroOperacion;
                String subject = "Envio devolucion a contabilidad N° operación:" + nroOperacion;
                Long userID = requestBody.get("userID") != null ? Long.parseLong(requestBody.get("userID").toString())
                        : null;
                System.out.println("nroOperacion222" + nroOperacion);
                System.out.println("estadowwwww" + lsestado);
                lcrReembolsoService.actualizaContabilidad(nroOperacion, lsestado);
                lcrReembolsoService.enviaContabilidad(nroOperacion, List.of("contabilidad"), subject, titulo, contenido,
                        2L, userID);

            } else {
                ObjectMapper mapper = new ObjectMapper();
                List<LCRReembolsoDTO> lista = mapper.convertValue(
                        listaObj,
                        new TypeReference<List<LCRReembolsoDTO>>() {
                        });
                System.out.println("lista objetos");
                for (LCRReembolsoDTO dto : lista) {
                    String titulo = "se envio información";
                    String contenido = "Se deriva a contalibilidad la devolución del codigo "
                            + dto.getLcrReembolso().getNumOperacion();
                    String subject = "Envio devolucion a contabilidad N° operación:"
                            + dto.getLcrReembolso().getNumOperacion();
                    Long userID = requestBody.get("userID") != null
                            ? Long.parseLong(requestBody.get("userID").toString())
                            : null;
                    System.out.println("nroOperacion" + dto.getLcrReembolso().getNumOperacion());
                    System.out.println("estado" + lsestado);
                    lcrReembolsoService.actualizaContabilidad(dto.getLcrReembolso().getNumOperacion(), lsestado);
                    System.out.println("pasoestado" + lsestado);
                    lcrReembolsoService.enviaContabilidad(dto.getLcrReembolso().getNumOperacion(),
                            List.of("administracion"), subject, titulo, contenido, 2L, userID);
                    System.out.println("enviaContabilidad" + lsestado);

                }
            }
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    
    @PostMapping("/soloflag")
    public ResponseEntity<ApiResponse> soloflag(@RequestBody Map<String, ?> requestBody) {
        try {
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            lcrReembolsoService.soloflag(nroOperacion);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

}
