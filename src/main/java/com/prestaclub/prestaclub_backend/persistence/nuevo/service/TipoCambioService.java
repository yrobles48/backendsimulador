package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TipoCambio;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.TipoCambioRepository;

@Service
public class TipoCambioService {

    @Autowired
    private TipoCambioRepository tipoCambioRepository;

    public TipoCambio saveTipoCambio(TipoCambio tipoCambio) {
        return tipoCambioRepository.save(tipoCambio);
    }

    public TipoCambio findAllByOrderByFechaDesc() {
        return tipoCambioRepository.findAllByOrderByFechaDesc();
        //return list.isEmpty() ? null : list.get(0);
    }

    public ApiResponseData<List<TipoCambio>> findLastTipoCambio() {
        ApiResponseData<List<TipoCambio>> apiResponseData = new ApiResponseData<>();
        try {
            List<TipoCambio> list = tipoCambioRepository.findLastTipoCambio();
            apiResponseData.setData(list);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return apiResponseData;
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return new ApiResponseData<>(e.getMessage(), false);
        }
    }
    
}
