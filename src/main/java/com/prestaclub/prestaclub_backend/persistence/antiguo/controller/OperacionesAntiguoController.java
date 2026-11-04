package com.prestaclub.prestaclub_backend.persistence.antiguo.controller;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionIDDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.PropietariosDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.OperacionRequest;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import software.amazon.awssdk.services.opensearch.model.SAMLIdp;

@RestController
@RequestMapping("/antiguo/operaciones")
@CrossOrigin("*")
public class OperacionesAntiguoController {

    @Autowired
    OperacionesAntiguoService operacionesAntiguoService;
    @PostMapping("/documento")
    public ResponseEntity<ApiResponseData<List<OperacionDTO>>> obtenerOperaciones(@RequestBody OperacionRequest operacionRequest) {
        ApiResponseData<List<OperacionDTO>> apiResponseData = new ApiResponseData<>();
        try { 
            if (operacionRequest.getNumeroOperacion() == 0 && operacionRequest.getTipoBusqueda().equals("operacion")) { 
                apiResponseData.setSuccess(false);
                apiResponseData.setMessage("No se encontraron resultados");
                apiResponseData.setData(null);
                return ResponseEntity.ok().body(apiResponseData);
            }
            String tipoBusqueda = operacionRequest.getTipoBusqueda();
            String documento = operacionRequest.getNumeroDocumento();
            List<OperacionDTO> operaciones = null;
            if (tipoBusqueda.equals("documento")) {
                // String tipoDocumento = operacionRequest.getTipoDocumento();
                operaciones = operacionesAntiguoService.obtenerOperacionesXNumeroDocumento(documento);
            }else{
                if (tipoBusqueda.equals("operacion")) {
                    Integer numeroOperacion = operacionRequest.getNumeroOperacion();
                    System.out.println("numeroOperacion"+numeroOperacion);
                    operaciones = operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numeroOperacion);
                }else{
                    apiResponseData.setSuccess(false);
                    apiResponseData.setMessage("Error al seleccionar el tipo de busqueda");
                    apiResponseData.setData(null);
                    return ResponseEntity.badRequest().body(apiResponseData);
                }
            }
            System.out.println("las operaciones"+operaciones.size());
            apiResponseData.setData(operaciones);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("Operaciones obtenidas"); 
            return ResponseEntity.ok().body(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage("Error al obtener las operaciones");
            apiResponseData.setData(null);
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }

    @PostMapping("/documentoid")
    public ResponseEntity<ApiResponseData<List<OperacionIDDTO>>> obtenerOperacionesID(@RequestBody OperacionRequest operacionRequest) {
        ApiResponseData<List<OperacionIDDTO>> apiResponseData = new ApiResponseData<>();
        try {
            if (operacionRequest.getNumeroOperacion() == 0 && operacionRequest.getTipoBusqueda().equals("operacion")) {
                apiResponseData.setSuccess(false);
                apiResponseData.setMessage("No se encontraron resultados");
                apiResponseData.setData(null);
                return ResponseEntity.ok().body(apiResponseData);
            }
            String tipoBusqueda = operacionRequest.getTipoBusqueda();
            String documento = operacionRequest.getNumeroDocumento();
            List<OperacionIDDTO> operaciones = null;
            if (tipoBusqueda.equals("documento")) {
                // String tipoDocumento = operacionRequest.getTipoDocumento();
                operaciones = operacionesAntiguoService.obtenerOperacionesXNumeroDocumentoID(documento);
            }else{
                if (tipoBusqueda.equals("operacion")) {
                    Integer numeroOperacion = operacionRequest.getNumeroOperacion();
                    System.out.println("numeroOperacion"+numeroOperacion);
                    operaciones = operacionesAntiguoService.obtenerOperacionesXNumeroOperacionID(numeroOperacion);
                }else{
                    apiResponseData.setSuccess(false);
                    apiResponseData.setMessage("Error al seleccionar el tipo de busqueda");
                    apiResponseData.setData(null);
                    return ResponseEntity.badRequest().body(apiResponseData);
                }
            }
            System.out.println("las operaciones"+operaciones.size());
            apiResponseData.setData(operaciones);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("Operaciones obtenidas");
            return ResponseEntity.ok().body(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage("Error al obtener las operaciones");
            apiResponseData.setData(null);
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }


    @PostMapping("/propietario")
    public ResponseEntity<ApiResponseData<List<PropietariosDTO>>> obtenerpropietarioID(@RequestBody OperacionRequest operacionRequest) {
        ApiResponseData<List<PropietariosDTO>> apiResponseData = new ApiResponseData<>();
        try {
            if (operacionRequest.getNumeroOperacion() == 0) {
                apiResponseData.setSuccess(false);
                apiResponseData.setMessage("No se encontraron resultados");
                apiResponseData.setData(null);
                return ResponseEntity.ok().body(apiResponseData);
            }
            String documento = operacionRequest.getNumeroDocumento();
            List<PropietariosDTO> operaciones = null;
            Integer numeroOperacion = operacionRequest.getNumeroOperacion();
            System.out.println("numeroOperacion"+numeroOperacion);
            operaciones = operacionesAntiguoService.getPropietariosXNumeroOperacion(numeroOperacion);
            System.out.println("las getPropietariosXNumeroOperacion"+operaciones.size());
            apiResponseData.setData(operaciones);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("Operaciones obtenidas");
            return ResponseEntity.ok().body(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage("Error al obtener las operaciones");
            apiResponseData.setData(null);
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }
}
