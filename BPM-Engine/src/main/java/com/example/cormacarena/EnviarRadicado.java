package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnviarRadicado implements JavaDelegate {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void execute(DelegateExecution execution) {
        String fechaRadicacion = (String) execution.getVariable("fechaRadicacion");
        String numeroRadicado = (String) execution.getVariable("numeroRadicado");

        String mensaje = "Hola, tu solicitud ha sido radicada correctamente. Número de radicado: "
                + numeroRadicado + ", el día: " + fechaRadicacion;

        // Enviar el mensaje al backend del usuario
        String url = "http://localhost:9090/api/bandeja";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("mensaje", mensaje);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            System.out.println("Mensaje enviado exitosamente a la bandeja del usuario.");
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje a la bandeja del usuario: " + e.getMessage());
        }

        // También puedes guardar el mensaje como variable del proceso si quieres
        execution.setVariable("mensajeUsuario", mensaje);
    }
}
