package com.prestaclub.prestaclub_backend.persistence.nuevo.scheduler;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TipoCambio;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.TipoCambioService;

@Service
public class TipoCambioScheduler {

    @Autowired
    private TipoCambioService tipoCambioService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String API_URL = "https://api.apis.net.pe/v1/tipo-cambio-sunat";


     @Scheduled(cron = "0 0 8 * * *") // Ejecuta todos los días a la medianoche
    public void registrarTipoCambioDiario() {
        System.out.println("Registrando tipo de cambio diario");

        Map<String, Object> tipoCambio = restTemplate.getForObject(API_URL, Map.class);

        if (tipoCambio != null) {
            //double compra = (Double) tipoCambio.get("compra");
            //double venta = (Double) tipoCambio.get("venta");
            Double ventaDouble = (Double) tipoCambio.get("venta"); // Obtenemos el valor como Double
            BigDecimal venta = BigDecimal.valueOf(ventaDouble);
            //String origen = (String) tipoCambio.get("origen");
            String moneda = (String) tipoCambio.get("moneda");
            String fecha = (String) tipoCambio.get("fecha");

            TipoCambio tc = new TipoCambio();
            tc.setMonedaBase("PEN");
            tc.setMonedaDestino(moneda);
            tc.setTipoCambio(venta);
            tc.setFecha(LocalDate.parse(fecha));
            tipoCambioService.saveTipoCambio(tc);
        } 
        Map<String, Object> payload = new HashMap<>(); 
        payload.put("usuario", null);
        payload.put("message", "El Sistema registró un nuevo tipo de cambio");
        messagingTemplate.convertAndSend("/topic/tipo-cambio/refresh", payload);
    } 
}
