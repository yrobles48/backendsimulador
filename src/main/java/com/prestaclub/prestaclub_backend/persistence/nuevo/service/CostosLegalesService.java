package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegalesHistory;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.CostosLegalesHistoryRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.CostosLegalesRepository;

@Service
public class CostosLegalesService {
    @Autowired
    private CostosLegalesRepository costosLegalesRepository;

    @Autowired
    private CostosLegalesHistoryRepository costosLegalesHistoryRepository;

    public List<CostosLegales> listar() {
        return costosLegalesRepository.findAll(Sort.by(Sort.Direction.ASC, "descripcion"));
    }

    public CostosLegales guardar(CostosLegales costosLegales) {
        return costosLegalesRepository.save(costosLegales);
    }   
 
    public CostosLegales findBySlug(String slug) {
        return costosLegalesRepository.findBySlug(slug);
    }

    public CostosLegales findCostosLegalesById(Long id) {
        return costosLegalesRepository.findCostosLegalesById(id);
    }

    public ApiResponse actualizar(Long id, BigDecimal valor, String slug,String tipo , Long userId) {
        CostosLegalesHistory costosLegalesHistory = new CostosLegalesHistory();
        CostosLegales costosLegales = costosLegalesRepository.findBySlug(slug);
        Usuario user = new Usuario();
        user.setId(userId);
        try {
            if (costosLegales == null) {
                return new ApiResponse("No se encontró el costo legal", false);
            }
            costosLegalesHistory.setOldValor(costosLegales.getValor());
            costosLegalesHistory.setUser(user);
            costosLegalesHistory.setCostosLegales(costosLegales);
            costosLegalesHistory.setNewValor(valor);
            costosLegalesHistory.setFecha(LocalDateTime.now());
            costosLegalesHistory.setTipo(tipo);

            costosLegales.setValor(valor);
            costosLegales = costosLegalesRepository.save(costosLegales);
     
            costosLegalesHistoryRepository.save(costosLegalesHistory);
    
            return new ApiResponse("Costo legal actualizado", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }
 
}
