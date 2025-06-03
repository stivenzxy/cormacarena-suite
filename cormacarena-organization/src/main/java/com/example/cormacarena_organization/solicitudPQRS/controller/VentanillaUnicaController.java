package com.example.cormacarena_organization.solicitudPQRS.controller;

import com.example.cormacarena_organization.solicitudPQRS.dto.VerificarCompetenciaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ventanilla")
public class VentanillaUnicaController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String mostrarListaTareas(Model model) {
        String camundaTaskUrl = "http://localhost:8080/engine-rest/task?taskDefinitionKey=Activity_0wymy7c";
        List<Map<String, Object>> tareas = restTemplate.getForObject(camundaTaskUrl, List.class);

        if (tareas == null || tareas.isEmpty()) {
            model.addAttribute("mensaje", "No hay tareas pendientes para Ventanilla Única.");
            return "pqrs/index";
        }

        // Crear lista enriquecida con variables
        List<Map<String, Object>> tareasEnriquecidas = new ArrayList<>();

        for (Map<String, Object> tarea : tareas) {
            String taskId = (String) tarea.get("id");
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            String tipoSolicitud = variables.containsKey("tipoSolicitud")
                    ? (String) variables.get("tipoSolicitud").get("value") : "No definido";

            String fechaSolicitud = variables.containsKey("fechaSolicitud")
                    ? (String) variables.get("fechaSolicitud").get("value") : "Sin fecha";

            tarea.put("tipoSolicitud", tipoSolicitud);
            tarea.put("fechaSolicitud", fechaSolicitud);
            tareasEnriquecidas.add(tarea);
        }

        // Ordenar por fecha de solicitud descendente
        tareasEnriquecidas.sort((a, b) -> {
            String fechaA = (String) a.get("fechaSolicitud");
            String fechaB = (String) b.get("fechaSolicitud");

            // Las fechas están en formato texto, pero si son ISO 8601 o yyyy-MM-dd, esto sirve:
            return fechaB.compareTo(fechaA); // orden descendente
        });

        model.addAttribute("tareas", tareasEnriquecidas);
        return "pqrs/ventanilla/ventanilla_lista";
    }



    @GetMapping("/resolver/{taskId}")
    public String mostrarFormulario(@PathVariable String taskId, Model model) {
        String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("tipoSolicitud", getVariableValue(variables, "tipoSolicitud"));
        model.addAttribute("nombre", getVariableValue(variables, "nombre"));
        model.addAttribute("email", getVariableValue(variables, "email"));
        model.addAttribute("descripcion", getVariableValue(variables, "descripcion"));
        model.addAttribute("adjDocumentos", getVariableValue(variables, "adjDocumentos"));
        model.addAttribute("nombreArchivo", getVariableValue(variables, "nombreArchivo"));
        model.addAttribute("verificarDTO", new VerificarCompetenciaDTO());
        model.addAttribute("fechaSolicitud", getVariableValue(variables, "fechaSolicitud"));

        return "pqrs/ventanilla/ventanilla_unica";
    }

    @PostMapping("/enviar")
    public String enviarFormulario(@RequestParam("taskId") String taskId,
                                   @ModelAttribute VerificarCompetenciaDTO dto,
                                   Model model) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("competencia", Map.of("value", dto.getCompetencia(), "type", "Boolean"));

        Map<String, Object> request = new HashMap<>();
        request.put("variables", variables);

        String camundaUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
        restTemplate.postForObject(camundaUrl, request, String.class);

        model.addAttribute("mensaje", "Formulario enviado correctamente a Camunda.");
        return "redirect:/ventanilla";
    }

    private Object getVariableValue(Map<String, Map<String, Object>> vars, String key) {
        return vars.containsKey(key) ? vars.get(key).get("value") : null;
    }
}