package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificarUsuarioService implements JavaDelegate {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void execute(DelegateExecution execution) {
        // Variables del proceso (respetando mayúsculas como están en el modelo BPMN)
        String nombre = (String) execution.getVariable("nombre");
        String tipoSolicitud = (String) execution.getVariable("tipoSolicitud");
        String numeroRadicado = (String) execution.getVariable("numeroRadicado");
        String fechaRadicacion = (String) execution.getVariable("fechaRadicacion");
        String descripcion = (String) execution.getVariable("descripcion");
        String respuesta = (String) execution.getVariable("respuestaSolicitud");
        String profesional = (String) execution.getVariable("profesional");
        String jefeDependencia = (String) execution.getVariable("jefeDependencia");
        String nombreArchivo = (String) execution.getVariable("nombreArchivo");

        // Crear mensaje completo
        String mensaje = String.format(
                "Hola %s, tu solicitud tipo \"%s\" con número de radicado %s fue recibida el día %s. " +
                        "Descripción: \"%s\". Respuesta: \"%s\". Profesional responsable: %s. Jefe de dependencia: %s. " +
                        "Documento anexado por usted: %s.",
                nombre, tipoSolicitud, numeroRadicado, fechaRadicacion, descripcion,
                respuesta, profesional, jefeDependencia, nombreArchivo
        );

        // Enviar a API del backend
        String url = "http://localhost:9090/api/bandeja";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("mensaje", mensaje);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            System.out.println("Mensaje enviado a la bandeja del usuario.");
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }

        // Guardar mensaje como variable del proceso (opcional)
        execution.setVariable("mensajeUsuario", mensaje);
    }
}
