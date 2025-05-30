package com.example.cormacarena_organization.solicitudPQRS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/jefe-dependencia")
public class JefeDependenciaController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String listarTareas(Model model) {
        // Modificado para incluir también las tareas de "Informar no competencia"
        String[] definiciones = {"Activity_03m82hr", "Activity_1tml0tf", "Activity_1wq7ur6", "Activity_1v96s1f"};
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

                // Asignar el nombre de actividad según el tipo de tarea
                // Asignar el nombre de actividad según el tipo de tarea
                String nombreActividad;
                if (defKey.equals("Activity_03m82hr")) {
                    nombreActividad = "Verificar Asignación";
                } else if (defKey.equals("Activity_1tml0tf")) {
                    nombreActividad = "Revisar Respuesta";
                } else if (defKey.equals("Activity_1wq7ur6")) {
                    nombreActividad = "Informar No Competencia";
                } else if (defKey.equals("Activity_1v96s1f")) {
                    nombreActividad = "Asignar Profesionales";
                } else {
                    nombreActividad = "Tarea Pendiente";
                }
                tarea.put("nombreActividad", nombreActividad);

                tarea.put("tipoSolicitud", variables.getOrDefault("tipoSolicitud", Map.of("value", "N/A")).get("value"));
                tarea.put("numeroRadicado", variables.getOrDefault("numeroRadicado", Map.of("value", "N/A")).get("value"));
                tarea.put("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
                tarea.put("fecha", variables.getOrDefault("fechaRadicacion", Map.of("value", "N/A")).get("value"));
                tarea.put("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));

                // Agregar fecha límite si está disponible
                if (variables.containsKey("due")) {
                    tarea.put("fechaLimite", variables.get("due").get("value"));
                }

                tareasTotales.add(tarea);
            }
        }

        if (tareasTotales.isEmpty()) {
            model.addAttribute("mensaje", "No hay tareas pendientes para el Jefe de Dependencia.");
            return "pqrs/index";
        } else {
            model.addAttribute("tareas", tareasTotales);
            return "pqrs/jefe-dependencia/jefe_dependencia_lista";
        }
    }

    /**
     * Método auxiliar para verificar si una tarea existe y está activa
     */
    private boolean verificarTareaExiste(String taskId) {
        try {
            String taskUrl = "http://localhost:8080/engine-rest/task/" + taskId;
            Map<String, Object> taskDetails = restTemplate.getForObject(taskUrl, Map.class);
            return taskDetails != null && taskDetails.get("id") != null;
        } catch (RestClientException e) {
            // Si hay error al consultar la tarea (404, etc.), significa que no existe
            return false;
        }
    }

    @GetMapping("/verificar/{taskId}")
    public String mostrarFormulario(@PathVariable String taskId, Model model) {
        // Verificar si la tarea existe antes de mostrar el formulario
        if (!verificarTareaExiste(taskId)) {
            model.addAttribute("mensaje", "La tarea solicitada ya no está disponible o ha expirado.");
            model.addAttribute("tipoMensaje", "warning");
            return "pqrs/mensaje"; // Vista genérica para mostrar mensajes
        }

        try {
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            model.addAttribute("taskId", taskId);
            model.addAttribute("tipoSolicitud", variables.getOrDefault("tipoSolicitud", Map.of("value", "N/A")).get("value"));
            model.addAttribute("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
            model.addAttribute("numeroRadicado", variables.getOrDefault("numeroRadicado", Map.of("value", "N/A")).get("value"));
            model.addAttribute("fechaRadicacion", variables.getOrDefault("fechaRadicacion", Map.of("value", "N/A")).get("value"));
            model.addAttribute("opciones", List.of("Si", "No"));
            return "pqrs/jefe-dependencia/jefe_dependencia_formulario";
        } catch (RestClientException e) {
            model.addAttribute("mensaje", "Error al cargar los datos de la tarea. La tarea puede haber expirado.");
            model.addAttribute("tipoMensaje", "error");
            return "pqrs/mensaje";
        }
    }

    @PostMapping("/verificar")
    public String procesarDecision(@RequestParam String taskId,
                                   @RequestParam String competenciaPersona) {

        // Verificar si la tarea aún existe antes de procesarla
        if (!verificarTareaExiste(taskId)) {
            // Redirigir con mensaje de error
            return "redirect:/jefe-dependencia?error=task_expired";
        }

        try {
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
                String queryUrl = String.join("",
                        "http://localhost:8080/engine-rest/task?",
                        "taskDefinitionKey=Activity_1wq7ur6",
                        "&processInstanceId=", processInstanceId
                );
                List<Map<String, Object>> nextTasks = restTemplate.getForObject(queryUrl, List.class);

                if (nextTasks != null && !nextTasks.isEmpty()) {
                    String nextTaskId = (String) nextTasks.get(0).get("id");
                    return "redirect:/jefe-dependencia/no-competencia/" + nextTaskId;
                }
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

            throw new IllegalStateException("No se encontró la tarea 'Asignar profesionales' para el proceso " + processInstanceId);

        } catch (RestClientException e) {
            return "redirect:/jefe-dependencia?error=task_processing_failed";
        }
    }

    @GetMapping("/no-competencia/{taskId}")
    public String mostrarFormularioNoCompetencia(@PathVariable String taskId, Model model) {
        // VALIDACIÓN CRÍTICA: Verificar si la tarea aún existe
        if (!verificarTareaExiste(taskId)) {
            model.addAttribute("mensaje", "Formulario no disponible");
            model.addAttribute("descripcionMensaje",
                    "Esta tarea ha expirado por tiempo límite o ya ha sido procesada por el sistema. " +
                            "La solicitud ha sido redirigida automáticamente al siguiente paso del proceso.");
            model.addAttribute("tipoMensaje", "warning");
            model.addAttribute("mostrarBotonVolver", true);
            model.addAttribute("urlVolver", "/jefe-dependencia");
            return "pqrs/mensaje";
        }

        try {
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            model.addAttribute("taskId", taskId);
            model.addAttribute("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));

            return "pqrs/jefe-dependencia/jefe_no_competencia_form";
        } catch (RestClientException e) {
            model.addAttribute("mensaje", "Error al cargar el formulario");
            model.addAttribute("descripcionMensaje", "No se pudieron cargar los datos de la tarea. La tarea puede haber expirado.");
            model.addAttribute("tipoMensaje", "error");
            model.addAttribute("mostrarBotonVolver", true);
            model.addAttribute("urlVolver", "/jefe-dependencia");
            return "pqrs/mensaje";
        }
    }

    @PostMapping("/no-competencia")
    public String procesarNoCompetencia(@RequestParam String taskId,
                                        @RequestParam String explicacionNoCompetencia) {

        // Verificar si la tarea aún existe antes de procesarla
        if (!verificarTareaExiste(taskId)) {
            return "redirect:/jefe-dependencia?error=task_expired&message=La tarea ha expirado y no puede ser procesada";
        }

        try {
            Map<String, Object> variables = Map.of(
                    "explicacionNoCompetencia", Map.of(
                            "value", explicacionNoCompetencia,
                            "type", "String"
                    )
            );

            Map<String, Object> body = Map.of("variables", variables);
            String url = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
            restTemplate.postForObject(url, body, String.class);

            return "redirect:/jefe-dependencia?success=task_completed";
        } catch (RestClientException e) {
            return "redirect:/jefe-dependencia?error=task_processing_failed&message=Error al procesar la tarea de no competencia";
        }
    }

    // Resto de métodos sin cambios...
    @GetMapping("/asignar-profesionales/{taskId}")
    public String mostrarFormularioAsignacion(@PathVariable String taskId, Model model) {
        if (!verificarTareaExiste(taskId)) {
            model.addAttribute("mensaje", "Formulario no disponible");
            model.addAttribute("tipoMensaje", "warning");
            return "pqrs/mensaje";
        }

        model.addAttribute("taskId", taskId);
        List<String> profesionales = List.of(
                "Ingeniero Ambiental",
                "Profesional Jurídico",
                "Ingeniero Forestal",
                "Profesional en Gestión Documental",
                "Biólogo"
        );

        Map<String, String> limitesTiempo = new LinkedHashMap<>();
        limitesTiempo.put("Un día", "PT1M");
        limitesTiempo.put("Dos días", "PT1M");
        limitesTiempo.put("Tres días", "PT1M");
        limitesTiempo.put("Cuatro días", "PT1M");

        model.addAttribute("profesionales", profesionales);
        model.addAttribute("limitesTiempo", limitesTiempo);

        return "pqrs/jefe-dependencia/asignar_profesionales_form";
    }

    @PostMapping("/asignar-profesionales")
    public String procesarAsignacion(@RequestParam String taskId,
                                     @RequestParam String profesional,
                                     @RequestParam String limiteTiempo) {

        if (!verificarTareaExiste(taskId)) {
            return "redirect:/jefe-dependencia?error=task_expired";
        }

        try {
            Map<String, Object> variables = Map.of(
                    "profesional", Map.of("value", profesional, "type", "String"),
                    "limiteTiempo", Map.of("value", limiteTiempo, "type", "String")
            );

            restTemplate.postForObject(
                    "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                    Map.of("variables", variables),
                    String.class
            );

            return "redirect:/jefe-dependencia?success=task_completed";
        } catch (RestClientException e) {
            return "redirect:/jefe-dependencia?error=task_processing_failed";
        }
    }

    @GetMapping("/revisar-respuesta/{taskId}")
    public String mostrarFormularioRevisar(@PathVariable String taskId, Model model) {
        if (!verificarTareaExiste(taskId)) {
            model.addAttribute("mensaje", "Formulario no disponible");
            model.addAttribute("tipoMensaje", "warning");
            return "pqrs/mensaje";
        }

        try {
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            model.addAttribute("taskId", taskId);
            model.addAttribute("profesional", variables.getOrDefault("profesional", Map.of("value", "N/A")).get("value"));
            model.addAttribute("descripcion", variables.getOrDefault("descripcion", Map.of("value", "N/A")).get("value"));
            model.addAttribute("respuestaSolicitud", variables.getOrDefault("respuestaSolicitud", Map.of("value", "N/A")).get("value"));

            return "pqrs/jefe-dependencia/revisar_respuesta_form";
        } catch (RestClientException e) {
            model.addAttribute("mensaje", "Error al cargar el formulario");
            model.addAttribute("tipoMensaje", "error");
            return "pqrs/mensaje";
        }
    }

    @PostMapping("/revisar-respuesta")
    public String procesarRevisarRespuesta(@RequestParam String taskId,
                                           @RequestParam boolean validezRespuesta) {

        if (!verificarTareaExiste(taskId)) {
            return "redirect:/jefe-dependencia?error=task_expired";
        }

        try {
            String taskUrl = "http://localhost:8080/engine-rest/task/" + taskId;
            Map<String, Object> taskDetails = restTemplate.getForObject(taskUrl, Map.class);
            String processInstanceId = (String) taskDetails.get("processInstanceId");

            Map<String, Object> variables = Map.of(
                    "validezRespuesta", Map.of("value", validezRespuesta, "type", "Boolean")
            );

            restTemplate.postForObject(
                    "http://localhost:8080/engine-rest/task/" + taskId + "/complete",
                    Map.of("variables", variables),
                    String.class
            );

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

            return "redirect:/jefe-dependencia?success=task_completed";
        } catch (RestClientException e) {
            return "redirect:/jefe-dependencia?error=task_processing_failed";
        }
    }

    @GetMapping("/firmar/{taskId}")
    public String mostrarFormularioFirma(@PathVariable String taskId, Model model) {
        if (!verificarTareaExiste(taskId)) {
            model.addAttribute("mensaje", "Formulario no disponible");
            model.addAttribute("tipoMensaje", "warning");
            return "pqrs/mensaje";
        }

        try {
            String variablesUrl = "http://localhost:8080/engine-rest/task/" + taskId + "/variables";
            Map<String, Map<String, Object>> variables = restTemplate.getForObject(variablesUrl, Map.class);

            model.addAttribute("taskId", taskId);
            model.addAttribute("jefeDependencia", variables.getOrDefault("jefeDependencia", Map.of("value", "N/A")).get("value"));
            return "pqrs/jefe-dependencia/firmar_respuesta_form";
        } catch (RestClientException e) {
            model.addAttribute("mensaje", "Error al cargar el formulario");
            model.addAttribute("tipoMensaje", "error");
            return "pqrs/mensaje";
        }
    }

    @PostMapping("/firmar")
    public String procesarFirma(@RequestParam String taskId,
                                @RequestParam String jefeDependencia,
                                @RequestParam String documentoResponsable,
                                @RequestParam String firma) {

        if (!verificarTareaExiste(taskId)) {
            return "redirect:/jefe-dependencia?error=task_expired";
        }

        try {
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

            return "redirect:/jefe-dependencia?success=task_completed";
        } catch (RestClientException e) {
            return "redirect:/jefe-dependencia?error=task_processing_failed";
        }
    }

    @GetMapping("/api/verificar-tarea/{taskId}")
    @ResponseBody
    public Map<String, Boolean> verificarTareaAPI(@PathVariable String taskId) {
        boolean existe = verificarTareaExiste(taskId);
        return Map.of("exists", existe);
    }
}
