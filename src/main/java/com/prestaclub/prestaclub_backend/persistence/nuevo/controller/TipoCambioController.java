package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TipoCambio;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.TipoCambioService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 


@RestController
@RequestMapping("nuevo/tipo-cambio")
public class TipoCambioController {

    @Autowired
    private TipoCambioService tipoCambioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public ResponseEntity<Object> obtenerTipoCambio() {
        TipoCambio tipoCambio = this.tipoCambioService.findAllByOrderByFechaDesc();
        return ResponseEntity.ok(tipoCambio);
    }

    @GetMapping("/ultimos")
    public ResponseEntity<ApiResponseData<List<TipoCambio>>> obtenerUltimosTipoCambio() { 
        return ResponseEntity.ok( this.tipoCambioService.findLastTipoCambio()); 
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addTipoCambio(@RequestBody Map<String, ?> requestBody){ 
        ApiResponse resp = new ApiResponse(); 
        TipoCambio tipoCambio = new TipoCambio();
        try { 
            BigDecimal tc = BigDecimal.valueOf(Double.parseDouble(requestBody.get("tipoCambio").toString()));
            @SuppressWarnings("null")
            Long userId = requestBody.get("userId") instanceof Integer ? ((Integer) requestBody.get("userId")).longValue() : requestBody.get("userId") instanceof Long ? (Long) requestBody.get("userId") : null;
            tipoCambio.setFecha(LocalDate.now());
            tipoCambio.setMonedaBase("PEN");
            tipoCambio.setMonedaDestino("USD");
            //tipoCambio.setTipoCambio(Double.parseDouble(String.format("%.3f", tipoCambio.getTipoCambio())));
            tipoCambio.setTipoCambio(tc);
            TipoCambio t = this.tipoCambioService.saveTipoCambio(tipoCambio);
            if (t != null) {
                resp.setSuccess(true);
                resp.setMessage("OK");
                Map<String, Object> payload = new HashMap<>();
                UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById( userId);
                payload.put("usuario", usuario);
                payload.put("message", "El Usuario " + usuario.getUsername() + " registró un nuevo tipo de cambio");
                messagingTemplate.convertAndSend("/topic/tipo-cambio/refresh", payload);
            }else{
                resp.setSuccess(false);
                resp.setMessage("ERROR AL INTENTAR GUARDAR");
            }
            return ResponseEntity.ok( resp);
        } catch (Exception e) { 
            resp.setSuccess(false);
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp); 
        }
    }

        private Map<String, Object> getPayloadSocketCronogramaRefresh(Long userId , Integer type ) {
        UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById( userId);
        String message = "";
        Map<String, Object> payload = new HashMap<>();
        payload.put("usuario", usuario);
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
        payload.put("message",message );
        return payload;
    }

    
}
