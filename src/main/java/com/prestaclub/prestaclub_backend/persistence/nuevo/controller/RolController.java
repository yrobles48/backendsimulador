package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.RolService; 
import org.springframework.web.bind.annotation.GetMapping; 


@RestController
@RequestMapping("nuevo/rol")
@CrossOrigin("*")
public class RolController {
    @Autowired
    private RolService rolService;

    @GetMapping("/lista")
    public ResponseEntity<ApiResponseData<List<Rol>>> getRolLista() {
        ApiResponseData<List<Rol>> apiResponseData = new ApiResponseData<>();
        try {
            List<Rol> roles = rolService.findAll();
            apiResponseData.setData(roles);
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
