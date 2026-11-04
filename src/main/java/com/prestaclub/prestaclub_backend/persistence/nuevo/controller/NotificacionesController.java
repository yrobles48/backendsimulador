package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.NotificacionesServices;

import software.amazon.awssdk.services.apigatewayv2.model.Api;

@RestController
@RequestMapping("/nuevo/notificaciones")
public class NotificacionesController {

    @Autowired
    private NotificacionesServices notificacionesServices;

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> listanNotificaciones(@RequestBody Map<String, ?> requestBody) { 
        try { 
            Integer pageSize = requestBody.get("pageSize") != null ? Integer.parseInt(requestBody.get("pageSize").toString()): 2;
            Integer pageNumber = requestBody.get("pageNumber") != null ? Integer.parseInt(requestBody.get("pageNumber").toString()): 1; 
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;
            
            ListaResponse lcr = notificacionesServices.listaNotificaciones(pageSize, pageNumber, userId);
            return ResponseEntity.ok(lcr);
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/contador-no-leidos")
    public ResponseEntity<ApiResponseData<Integer>> getCounterNotification(@RequestBody Map<String, ?> requestBody) { 
        ApiResponseData<Integer> response = new ApiResponseData<>();
        try { 
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;
            
            Integer contador = notificacionesServices.contadorNotificacionesNoLeidos(userId);
            
            response.setData(contador);
            response.setSuccess(true);
            response.setMessage("OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) { 
            response.setData(null);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update/leido")
    public ResponseEntity<ApiResponse> updateLeido(@RequestBody Map<String, ?> requestBody) {  
        try { 
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null;
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null;
            
            ApiResponse response = notificacionesServices.updateLeido(id, userId); 
            return ResponseEntity.ok(response);
        } catch (Exception e) {  
            e.printStackTrace();
            return ResponseEntity.badRequest().body( new ApiResponse( false, e.getMessage()));
        }
    }

    @PostMapping("/update/all-views")
    public ResponseEntity<ApiResponse> allViews(@RequestBody Map<String, ?> requestBody) {  
        try { 
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString()): null; 
            
            ApiResponse response = notificacionesServices.allViews(userId); 
            return ResponseEntity.ok(response);
        } catch (Exception e) {  
            e.printStackTrace();
            return ResponseEntity.badRequest().body( new ApiResponse( false, e.getMessage()));
        }
    }

}
