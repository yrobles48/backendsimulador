package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegalesHistory;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.CostosLegalesHistoryRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl; 
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.CostosLegalesHistoryDTO;

@Service
public class CostosLegalesHistoryService {
    @Autowired
    private CostosLegalesHistoryRepository costosLegalesHistoryRepository;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private CostosLegalesService costosLegalesService;

    public CostosLegalesHistory guardar(CostosLegalesHistory costosLegalesHistory) {
        return costosLegalesHistoryRepository.save(costosLegalesHistory);
    }
    
    public ListaResponse listaCostosLegalesHistory(Integer pageSize, Integer pageNumber, String filterText, String tipo) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize); 
        Page<CostosLegalesHistoryDTO> costosLegalesHistory = costosLegalesHistoryRepository.getListaCostosLegalesHistory( tipo, filterText, pageable);
        if (costosLegalesHistory.isEmpty()) {
            ListaResponse response = new ListaResponse();
            response.setData(null);
            response.setTotal(0);
            response.setPageNumber(pageNumber);  
            response.setPageSize(pageSize);
            response.setSuccess(false);
            response.setMessage("No se encontraron registros");
            return response;
        }
         
        
        ListaResponse response = new ListaResponse();
        response.setData(costosLegalesHistory.toList());
        response.setTotal((int)costosLegalesHistory.getTotalElements());
        response.setPageNumber(pageNumber);  
        response.setPageSize(pageSize);
        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }

}
 