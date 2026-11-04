package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.dto.UsuarioResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

@RestController
@RequestMapping("nuevo/usuario")
@CrossOrigin("*")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/")
    public Usuario guardarUsuario(@RequestBody Usuario usuario) throws Exception {
        
        usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
    
        return usuarioService.guardarUsuario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseData<UsuarioResponse>> obtenerUsuario(@PathVariable("id") Long id) { 
        ApiResponseData<UsuarioResponse> apiResponseData = new ApiResponseData<>();
        try {
            UsuarioResponse response = usuarioService.obtenerUsuarioById(id);
            apiResponseData.setData(response);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) { 
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public void EliminarUsuario(@PathVariable("id") Long id) {
        usuarioService.EliminarUsuario(id);
    }

    @PostMapping("/lista")
    public ResponseEntity<ListaResponse> listaUsuarios(@RequestBody Map<String, String> requestBody) {
        ListaResponse response = new ListaResponse();
        Integer pageSize = requestBody.containsKey("pageSize") ? Integer.parseInt(requestBody.get("pageSize")) : 2;
        Integer pageNumber = requestBody.containsKey("pageNumber") ? Integer.parseInt(requestBody.get("pageNumber")) : 1;
        String filterText = requestBody.containsKey("filterText") ? requestBody.get("filterText") : "";
        try {
            response = usuarioService.listaUsuarios(pageSize, pageNumber, filterText); 
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/status/update")
    public ResponseEntity<ApiResponse> changeStatus(@RequestBody Map<String, ?> requestBody) { 
        try {
            Boolean status = requestBody.get("estado") != null ? (Boolean)requestBody.get("estado") : false;
             @SuppressWarnings("null")
            Long id = requestBody.get("id") instanceof Integer ? ((Integer) requestBody.get("id")).longValue() :  requestBody.get("id") instanceof Long ? (Long) requestBody.get("id") : null;
            @SuppressWarnings("null")
            Long userId = requestBody.get("user_id") instanceof Integer ? ((Integer) requestBody.get("user_id")).longValue() : requestBody.get("user_id") instanceof Long ? (Long) requestBody.get("user_id") : null;
            ApiResponse apiResponse = usuarioService.changeStatus(status, id, userId);
            if (apiResponse.isSuccess() && !status) {
                messagingTemplate.convertAndSend("/topic/full/user-refresh",  getPayloadSocketRefresh(id, userId,0)); 
            }
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    private Map<String, Object> getPayloadSocketRefresh(  Long id, Long userId , Integer type ) {
        UsuarioMantenimientoDTO usuario = usuarioService.obtenerUsuarioMantenimientoById( userId);
        String message = "";
        Map<String, Object> payload = new HashMap<>();
        payload.put("usuario", usuario);
        payload.put("target_id", id);
        switch (type) {
            case 0:
                message = "El Usuario " + usuario.getUsername() + " Inactivó tu cuenta";
                break;
            default:
                break;
        }
        payload.put("message",message );
        return payload;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody Map<String, ?> requestBody) {
        try {
            Long user_id = requestBody.get("user_id") instanceof Integer ? ((Integer) requestBody.get("user_id")).longValue() : requestBody.get("user_id") instanceof Long ? (Long) requestBody.get("user_id") : null;
            String nombres = requestBody.get("nombres") instanceof String ? (String) requestBody.get("nombres") : null;
            String apellidPaterno = requestBody.get("apellido_paterno") instanceof String ? (String) requestBody.get("apellido_paterno") : null;
            String apellidMaterno = requestBody.get("apellido_materno") instanceof String ? (String) requestBody.get("apellido_materno") : null;
            String email = requestBody.get("correo") instanceof String ? (String) requestBody.get("correo") : null;
            ApiResponse apiResponse = usuarioService.addUser(nombres, apellidPaterno, apellidMaterno, email, user_id);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(apiResponse);
        }

    } 


    @PostMapping("/notificacion-correo/update")
    public ResponseEntity<ApiResponse> updateNotificacionCorreo(@RequestBody Map<String, ?> requestBody) { 
        try {
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()): null;
            Boolean notificacionCorreo = requestBody.get("notificacionCorreo") != null ? Boolean.parseBoolean(requestBody.get("notificacionCorreo").toString()): null;
            ApiResponse apiResponse = usuarioService.updateNotificacionCorreo(id, notificacionCorreo);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
    
    
}
