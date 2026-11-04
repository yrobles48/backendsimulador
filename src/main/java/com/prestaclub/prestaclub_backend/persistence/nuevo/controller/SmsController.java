package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import com.prestaclub.prestaclub_backend.persistence.nuevo.service.SmsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/enviar")
    public String enviarSms(@RequestBody Map<String, ?> requestBody) {

        String numero = requestBody.get("numero") != "" ? requestBody.get("numero").toString(): null;
        String mensaje = requestBody.get("mensaje") != "" ? requestBody.get("mensaje").toString(): null;

        return smsService.enviarSms(numero, mensaje);
    }

}
