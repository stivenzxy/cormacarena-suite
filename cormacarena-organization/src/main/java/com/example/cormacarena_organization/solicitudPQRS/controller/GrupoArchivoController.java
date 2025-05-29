package com.example.cormacarena_organization.solicitudPQRS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/grupo-archivo")
public class GrupoArchivoController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String mostrarTareas(Model model) {
        String url = "http://localhost:8080/engine-rest/task?taskDefinitionKey=Activity_0yjqid2";
        List<Map<String, Object>> tareas = restTemplate.getForObject(url, List.class);

        if (tareas == null || tareas.isEmpty()) {
            model.addAttribute("mensaje", "No hay tareas pendientes para el Grupo de Archivo.");
            return "pqrs/index"; // Redirige a la vista principal
        }


        List<Map<String, Object>> tareasEnriquecidas = new ArrayList<>();

        for (Map<String, Object> tarea : tareas) {
            String taskId = (String) tarea.get("id");
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            String tipoSolicitud = (String) variables.getOrDefault("tipoSolicitud", Map.of("value", "Desconocido")).get("value");
            String nombre = (String) variables.getOrDefault("nombre", Map.of("value", "N/A")).get("value");
            String descripcion = (String) variables.getOrDefault("descripcion", Map.of("value", "Sin descripción")).get("value");
            String fecha = (String) variables.getOrDefault("fechaRadicacion", Map.of("value", "1900-01-01")).get("value");

            tarea.put("tipoSolicitud", tipoSolicitud);
            tarea.put("nombre", nombre);
            tarea.put("descripcion", descripcion);
            tarea.put("fecha", fecha);
            tareasEnriquecidas.add(tarea);
        }

        // Ordenar por fecha descendente (de más reciente a más antigua)
        tareasEnriquecidas.sort((a, b) -> {
            String fechaA = (String) a.get("fecha");
            String fechaB = (String) b.get("fecha");
            return fechaB.compareTo(fechaA);
        });

        model.addAttribute("tareas", tareasEnriquecidas);
        return "pqrs/grupo-archivo/grupo_archivo_lista";
    }


    @GetMapping("/asignar/{taskId}")
    public String mostrarFormularioAsignacion(@PathVariable String taskId, Model model) {
        List<String> posiblesJefes = List.of(
                "Carlos Gómez", "Luis Rodríguez", "Andrés Torres", "Jorge Ramírez", "Felipe Morales",
                "María López", "Ana Castillo", "Sofía Méndez", "Laura Ríos", "Camila Vargas"
        );

        model.addAttribute("taskId", taskId);
        model.addAttribute("jefes", posiblesJefes);
        return "pqrs/grupo-archivo/asignar_jefe_dependencia";
    }


    @PostMapping("/asignar")
    public String procesarAsignacion(@RequestParam String taskId, @RequestParam String jefeAsignado) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("jefeDependencia", Map.of("value", jefeAsignado, "type", "String"));

        Map<String, Object> body = new HashMap<>();
        body.put("variables", variables);

        String url = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
        restTemplate.postForObject(url, body, String.class);

        return "redirect:/grupo-archivo";
    }
}
