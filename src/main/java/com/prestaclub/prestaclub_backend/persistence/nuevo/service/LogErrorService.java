package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LogError;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LogErrorRepository;

@Service
public class LogErrorService {
    @Autowired
    private LogErrorRepository logErrorRepository;

    public void guardar(Exception ex, String ruta) {
        LogError log = new LogError();
        log.setFecha(LocalDateTime.now());
        log.setRuta(ruta);
        log.setMensaje(ex.getMessage());
        log.setStacktrace(getStackTraceAsString(ex));

        if (ex.getStackTrace().length > 0) {
            log.setClase(ex.getStackTrace()[0].getClassName());
            log.setMetodo(ex.getStackTrace()[0].getMethodName());
        }

        logErrorRepository.save(log);
    }

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
}
