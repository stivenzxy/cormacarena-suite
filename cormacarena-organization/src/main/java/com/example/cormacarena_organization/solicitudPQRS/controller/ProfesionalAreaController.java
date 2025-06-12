package com.example.cormacarena_organization.solicitudPQRS.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profesional-area")
public class ProfesionalAreaController {

    @Value("${camunda.url}")
    private String camundaUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String listarSolicitudes(Model model) {
        String url = camundaUrl+"task?taskDefinitionKey=Activity_1uzqpdw";
        List<Map<String, Object>> tareas = restTemplate.getForObject(url, List.class);

        if (tareas == null || tareas.isEmpty()) {
            model.addAttribute("mensaje", "No hay solicitudes pendientes por responder.");
            return "pqrs/index"; // Redirige al index.html
        }


        List<Map<String, Object>> tareasEnriquecidas = new ArrayList<>();

        for (Map<String, Object> tarea : tareas) {
            String taskId = (String) tarea.get("id");
            String variablesUrl = camundaUrl+"task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            String limiteTiempoRaw = (String) variables.getOrDefault("limiteTiempo", Map.of("value", "N/A")).get("value");
            String limiteTiempoLegible = convertirDuracion(limiteTiempoRaw);

            tarea.put("taskId", taskId);
            tarea.put("tipoSolicitud", variables.getOrDefault("tipoSolicitud", Map.of("value", "N/A")).get("value"));
            tarea.put("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
            tarea.put("profesional", variables.getOrDefault("profesional", Map.of("value", "N/A")).get("value"));
            tarea.put("limiteTiempo", limiteTiempoLegible);

            tareasEnriquecidas.add(tarea);
        }


        model.addAttribute("tareas", tareasEnriquecidas);
        return "pqrs/profesional-area/profesional_area_lista";
    }

    @GetMapping("/responder/{taskId}")
    public String mostrarFormulario(@PathVariable String taskId, Model model) {
        String variablesUrl = camundaUrl+"task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("tipoSolicitud", variables.get("tipoSolicitud").get("value"));
        model.addAttribute("descripcion", variables.get("descripcion").get("value"));

        return "pqrs/profesional-area/profesional_area_formulario";
    }

    @PostMapping("/responder")
    public String procesarRespuesta(@RequestParam String taskId,
                                    @RequestParam String respuestaSolicitud) {

        Map<String, Object> variables = Map.of(
                "respuestaSolicitud", Map.of("value", respuestaSolicitud, "type", "String")
        );

        restTemplate.postForObject(
                camundaUrl+"task/" + taskId + "/complete",
                Map.of("variables", variables),
                String.class
        );

        return "redirect:/profesional-area";
    }
    private String convertirDuracion(String isoDuracion) {
        return switch (isoDuracion) {
            case "PT24H" -> "1 día";
            case "PT48H" -> "2 días";
            case "PT72H" -> "3 días";
            case "PT96H" -> "4 días";
            default -> "Duración desconocida";
        };
    }

}
