package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping(value = "/bitrix", consumes = {"application/x-www-form-urlencoded", "application/json"})
    public String postMethodName(@RequestParam Map<String, String> payload) {
        System.out.println("📩 Webhook recibido: " + payload);

        // Mostrar el evento recibido
        if (payload.containsKey("event")) {
            System.out.println("🔔 Evento recibido: " + payload.get("event"));
        }

        return "Webhook recibido con éxito";
    }
    
}
