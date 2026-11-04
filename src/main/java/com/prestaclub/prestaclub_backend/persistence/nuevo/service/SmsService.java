package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsService {

    @Value("${clicklab.api.key}")
    private String apiKey;

    @Value("${clicklab.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String enviarSms(String numero, String mensaje) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "App " + apiKey);
        Map<String, Object> body = new HashMap<>();
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> msg = new HashMap<>();
        msg.put("from", "Prestaclub");  // o lo que tengas como remitente aprobado
        msg.put("destinations", List.of(Map.of("to", numero)));
        msg.put("text", mensaje);
        messages.add(msg);
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
/*        try {
            ResponseEntity<String> response = rest.postForEntity(apiUrl, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        ResponseEntity<String> response =
                restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}