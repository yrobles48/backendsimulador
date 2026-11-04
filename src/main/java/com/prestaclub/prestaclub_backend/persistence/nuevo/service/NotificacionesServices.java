package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.CostosLegalesHistoryDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.NotificacionDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;

@Service
public class NotificacionesServices {

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    public ListaResponse listaNotificaciones(Integer pageSize, Integer pageNumber, Long userId) { 
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize); 
        Page<NotificacionDTO> notificaciones = notificacionesRepository.getNotificaciones(userId, pageable); 
        if (notificaciones.isEmpty()) {
            ListaResponse response = new ListaResponse();
            //mandar array vacio
            response.setData(Arrays.asList());  
            response.setTotal(0);
            response.setPageNumber(pageNumber);  
            response.setPageSize(pageSize);
            response.setSuccess(true);
            response.setMessage("No se encontraron registros");
            return response;
        }
         
        
        ListaResponse response = new ListaResponse();
        response.setData(notificaciones.toList());
        response.setTotal((int)notificaciones.getTotalElements());
        response.setPageNumber(pageNumber);  
        response.setPageSize(pageSize);
        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }


    public Integer contadorNotificacionesNoLeidos(Long userId) { 
        Integer contador = notificacionesRepository.contadorNotificacionesNoLeidos(userId);  
        return contador;
    }

    public ApiResponse updateLeido(Long id, Long userId) { 
        ApiResponse response = new ApiResponse();
        try {
            NotificacionesUsuarios notificacionesUsuarios = notificacionesUsuariosRepository.findByUserIdAndNotificacionId(userId, id);
            notificacionesUsuarios.setIsView(true);
            notificacionesUsuariosRepository.save(notificacionesUsuarios);
            response.setSuccess(true);
            response.setMessage("ok");
            return response;
        } catch (Exception e) { 
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }


    public ApiResponse allViews(Long userId) { 
        ApiResponse response = new ApiResponse();
        try {
            int contador = notificacionesUsuariosRepository.allViews(userId); 
            if (contador > 0) {
                response.setSuccess(true);
                response.setMessage("ok");
                return response;
            }else{
                response.setSuccess(false);
                response.setMessage("no se actualizó ninguna fila");
                return response;
            }
        } catch (Exception e) { 
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
