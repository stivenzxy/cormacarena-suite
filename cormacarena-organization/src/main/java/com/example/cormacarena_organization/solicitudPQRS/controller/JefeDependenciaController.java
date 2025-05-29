package com.example.cormacarena_organization.solicitudPQRS.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/jefe-dependencia")
public class JefeDependenciaController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String listarTareas(Model model) {
        String[] definiciones = {"Activity_03m82hr", "Activity_1tml0tf"};
        List<Map<String, Object>> tareasTotales = new ArrayList<>();

        for (String defKey : definiciones) {
            String url = "http://localhost:8080/engine-rest/task?taskDefinitionKey=" + defKey;
            List<Map<String, Object>> tareas = restTemplate.getForObject(url, List.class);
            if (tareas == null) continue;

            for (Map<String, Object> tarea : tareas) {
                String taskId = (String) tarea.get("id");
                String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
                Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

                tarea.put("taskId", taskId);
                tarea.put("taskDefinitionKey", defKey);
                tarea.put("nombreActividad", defKey.equals("Activity_03m82hr") ? "Verificar Asignación" : "Revisar Respuesta");
                tarea.put("tipoSolicitud", variables.getOrDefault("tipoSolicitud", Map.of("value", "N/A")).get("value"));
                tarea.put("numeroRadicado", variables.getOrDefault("numeroRadicado", Map.of("value", "N/A")).get("value"));
                tarea.put("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
                tarea.put("fecha", variables.getOrDefault("fechaRadicacion", Map.of("value", "N/A")).get("value"));
                tarea.put("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));
                tareasTotales.add(tarea);
            }
        }

        if (tareasTotales.isEmpty()) {
            model.addAttribute("mensaje", "No hay tareas pendientes para el Jefe de Dependencia.");
            return "pqrs/index"; // Redirige al index.html
        } else {
            model.addAttribute("tareas", tareasTotales);
            return "pqrs/jefe-dependencia/jefe_dependencia_lista";
        }
    }


        @GetMapping("/verificar/{taskId}")
    public String mostrarFormulario(@PathVariable String taskId, Model model) {
        String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("tipoSolicitud", variables.getOrDefault("tipoSolicitud", Map.of("value", "N/A")).get("value"));
        model.addAttribute("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
        model.addAttribute("numeroRadicado", variables.getOrDefault("numeroRadicado", Map.of("value", "N/A")).get("value"));
        model.addAttribute("fechaRadicacion", variables.getOrDefault("fechaRadicacion", Map.of("value", "N/A")).get("value"));
        model.addAttribute("opciones", List.of("Si", "No"));
        return "pqrs/jefe-dependencia/jefe_dependencia_formulario";
    }

    @PostMapping("/verificar")
    public String procesarDecision(@RequestParam String taskId,
                                   @RequestParam String competenciaPersona) {

        // 1. Obtener instancia de proceso antes de completar
        String taskUrl = "http://localhost:8080/engine-rest/task/" + taskId;
        Map<String, Object> taskDetails = restTemplate.getForObject(taskUrl, Map.class);
        String processInstanceId = (String) taskDetails.get("processInstanceId");

        // 2. Completar la tarea actual
        Map<String, Object> variables = new HashMap<>();
        variables.put("competenciaPersona", Map.of("value", competenciaPersona, "type", "String"));
        restTemplate.postForObject(
                "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                Map.of("variables", variables),
                String.class
        );

        // 3. Si rechazó la asignación, buscar la tarea de "Informar no competencia"
        if ("false".equals(competenciaPersona)) {
            // Query directo a Camunda por taskDefinitionKey + processInstanceId
            String queryUrl = String.join("",
                    "http://localhost:8080/engine-rest/task?",
                    "taskDefinitionKey=Activity_1wq7ur6",
                    "&processInstanceId=", processInstanceId
            );
            List<Map<String, Object>> nextTasks = restTemplate
                    .getForObject(queryUrl, List.class);

            if (nextTasks != null && !nextTasks.isEmpty()) {
                String nextTaskId = (String) nextTasks.get(0).get("id");
                return "redirect:/jefe-dependencia/no-competencia/" + nextTaskId;
            }
            // Si por alguna razón no la encuentra, puedes redirigir al listado o lanzar excepción
            throw new IllegalStateException(
                    "No se encontró la tarea de Informar No Competencia para el proceso " + processInstanceId
            );
        }

        // 4. Si confirmó la asignación, buscar la tarea "Asignar profesionales"
        String asignarProfesionalesUrl = String.join("",
                "http://localhost:8080/engine-rest/task?",
                "taskDefinitionKey=Activity_1v96s1f",
                "&processInstanceId=", processInstanceId
        );

        List<Map<String, Object>> tareasAsignar = restTemplate.getForObject(asignarProfesionalesUrl, List.class);

        if (tareasAsignar != null && !tareasAsignar.isEmpty()) {
            String siguienteTaskId = (String) tareasAsignar.get(0).get("id");
            return "redirect:/jefe-dependencia/asignar-profesionales/" + siguienteTaskId;
        }

        // Si no encuentra la tarea, puede retornar al listado
        throw new IllegalStateException("No se encontró la tarea 'Asignar profesionales' para el proceso " + processInstanceId);

    }




    @GetMapping("/no-competencia/{taskId}")
    public String mostrarFormularioNoCompetencia(@PathVariable String taskId, Model model) {
        String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));

        return "pqrs/jefe-dependencia/jefe_no_competencia_form";
    }

    @PostMapping("/no-competencia")
    public String procesarNoCompetencia(@RequestParam String taskId,
                                        @RequestParam String explicacionNoCompetencia) {

        Map<String, Object> variables = Map.of(
                "explicacionNoCompetencia", Map.of(
                        "value", explicacionNoCompetencia,
                        "type", "String"
                )
        );

        Map<String, Object> body = Map.of("variables", variables);

        String url = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
        restTemplate.postForObject(url, body, String.class);

        return "redirect:/jefe-dependencia";
    }


    @GetMapping("/asignar-profesionales/{taskId}")
    public String mostrarFormularioAsignacion(@PathVariable String taskId, Model model) {
        model.addAttribute("taskId", taskId);

        List<String> profesionales = List.of(
                "Ingeniero Ambiental",
                "Profesional Jurídico",
                "Ingeniero Forestal",
                "Profesional en Gestión Documental",
                "Biólogo"
        );

        Map<String, String> limitesTiempo = new LinkedHashMap<>();
        limitesTiempo.put("Un día", "PT24H");
        limitesTiempo.put("Dos días", "PT48H");
        limitesTiempo.put("Tres días", "PT72H");
        limitesTiempo.put("Cuatro días", "PT96H");


        model.addAttribute("profesionales", profesionales);
        model.addAttribute("limitesTiempo", limitesTiempo);

        return "pqrs/jefe-dependencia/asignar_profesionales_form";
    }


    @PostMapping("/asignar-profesionales")
    public String procesarAsignacion(@RequestParam String taskId,
                                     @RequestParam String profesional,
                                     @RequestParam String limiteTiempo) {

        Map<String, Object> variables = Map.of(
                "profesional", Map.of("value", profesional, "type", "String"),
                "limiteTiempo", Map.of("value", limiteTiempo, "type", "String")
        );

        restTemplate.postForObject(
                "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                Map.of("variables", variables),
                String.class
        );

        return "redirect:/jefe-dependencia";
    }


    @GetMapping("/revisar-respuesta/{taskId}")
    public String mostrarFormularioRevisar(@PathVariable String taskId, Model model) {
        String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("profesional", variables.getOrDefault("profesional", Map.of("value", "N/A")).get("value"));
        model.addAttribute("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
        model.addAttribute("respuestaSolicitud", variables.getOrDefault("respuestaSolicitud", Map.of("value", "N/A")).get("value"));

        return "pqrs/jefe-dependencia/revisar_respuesta_form";
    }


    @PostMapping("/revisar-respuesta")
    public String procesarRevisarRespuesta(@RequestParam String taskId,
                                           @RequestParam boolean validezRespuesta) {

        // Obtener el proceso asociado
        String taskUrl = "http://localhost:8080/engine-rest/task/" + taskId;
        Map<String, Object> taskDetails = restTemplate.getForObject(taskUrl, Map.class);
        String processInstanceId = (String) taskDetails.get("processInstanceId");

        // Completar la revisión
        Map<String, Object> variables = Map.of(
                "validezRespuesta", Map.of("value", validezRespuesta, "type", "Boolean")
        );

        restTemplate.postForObject(
                "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                Map.of("variables", variables),
                String.class
        );

        // Si es válida, buscar la tarea de firma
        if (validezRespuesta) {
            String buscarFirmaUrl = String.format(
                    "http://localhost:8080/engine-rest/task?taskDefinitionKey=Activity_0607mqq&processInstanceId=%s",
                    processInstanceId
            );
            List<Map<String, Object>> tareasFirma = restTemplate.getForObject(buscarFirmaUrl, List.class);

            if (tareasFirma != null && !tareasFirma.isEmpty()) {
                String siguienteTaskId = (String) tareasFirma.get(0).get("id");
                return "redirect:/jefe-dependencia/firmar/" + siguienteTaskId;
            }

            throw new IllegalStateException("No se encontró la tarea 'Firmar aprobación' para el proceso " + processInstanceId);
        }

        // Si no es válida, volver al listado
        return "redirect:/jefe-dependencia";
    }


    @GetMapping("/firmar/{taskId}")
    public String mostrarFormularioFirma(@PathVariable String taskId, Model model) {
        String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
        Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

        model.addAttribute("taskId", taskId);
        model.addAttribute("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));
        return "pqrs/jefe-dependencia/firmar_respuesta_form";
    }

    @PostMapping("/firmar")
    public String procesarFirma(@RequestParam String taskId,
                                @RequestParam String jefeDependencia,
                                @RequestParam String documentoResponsable,
                                @RequestParam String firma) {

        Map<String, Object> variables = Map.of(
                "jefeDependencia", Map.of("value", jefeDependencia, "type", "String"),
                "documentoResponsable", Map.of("value", documentoResponsable, "type", "String"),
                "firma", Map.of("value", firma, "type", "String")
        );

        restTemplate.postForObject(
                "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                Map.of("variables", variables),
                String.class
        );

        return "redirect:/jefe-dependencia";
    }

}
