package com.example.cormacarena_organization.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.*;
import com.example.cormacarena_organization.licenciamientoAmbiental.enums.SectorProyecto;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ActualizarEstadoService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ProfesionalRevSolService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfesionalRevSolServiceImpl implements ProfesionalRevSolService {

    private final RestTemplate restTemplate;
    private List<TaskInfo> tasksList = new ArrayList<>();
    private final ActualizarEstadoService actualizarEstadoService;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;

    @Override
    public TaskInfo getTaskInfoByProcessId(String idProceso) {
        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl+"task?processInstanceId="+idProceso, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            List<Map> tasks = response.getBody();

            if (tasks == null || tasks.isEmpty()) {
                response = restTemplate.exchange(camundaUrl+"task?superProcessInstance="+idProceso, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
                tasks = response.getBody();
            }

            if (tasks != null && !tasks.isEmpty()) {
                Map<String, String> taskInfoMap = new HashMap<>();

                taskInfoMap.put("taskId", String.valueOf(tasks.getFirst().get("id")));
                taskInfoMap.put("taskName", String.valueOf(tasks.getFirst().get("name")));
                taskInfoMap.put("assignee", String.valueOf(tasks.getFirst().get("assignee")));

                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setProcessId(idProceso);
                taskInfo.setTaskId(taskInfoMap.get("taskId"));
                taskInfo.setTaskName(taskInfoMap.get("taskName"));
                taskInfo.setTaskAssignee(taskInfoMap.get("assignee"));

                this.tasksList.add(taskInfo);
                return taskInfo;
            } else {
                System.err.println("No existe un proceso con el ID: " + idProceso);
                return null;
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error al realizar la petición a Camunda: " + errorMessage);
            return null;
        }
    }


    @Override
    public String getTaskIdByProcessIdWithApi(String idProceso) {
        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl + "task?processInstanceId=" + idProceso, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {
            });
            List<Map> tasks = response.getBody();

            if (tasks != null && !tasks.isEmpty()) {
                return String.valueOf(tasks.getFirst().get("id"));
            } else {
                System.err.println("Proceso no encontrado: " + idProceso);
                return null;
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error al realizar la petición a Camunda: " + errorMessage);
            return null;
        }
    }

    @Override
    public void setAssignee(String taskId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl+"task/"+taskId+"/assignee", HttpMethod.POST, requestEntity, String.class);
            System.out.println("Rol asignado exitosamente: " + response.getBody());
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error in the Camunda request: " + errorMessage);
        }
    }

    @Override
    public SolicitudDTO getProcessVariablesById(String idProceso) {
        String apiRequestUrl = camundaUrl + "process-instance/" + idProceso + "/variables?deserializeValues=true";

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                apiRequestUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> variablesMap = responseEntity.getBody();

        if (variablesMap != null) {
            SolicitudDTO solicitud = new SolicitudDTO();

            solicitud.setNombreSolicitante(getVariableValue(variablesMap, "nombreSolicitante"));
            solicitud.setEmail(getVariableValue(variablesMap, "email"));
            solicitud.setNombreProyecto(getVariableValue(variablesMap, "nombreProyecto"));
            solicitud.setEstado(getVariableValue(variablesMap, "estado"));
            solicitud.setProfesionalAsignado(getVariableValue(variablesMap, "profesionalAsignado"));
            solicitud.setFechaVisitaTecnica(getVariableValue(variablesMap, "fechaVisitaTecnica"));
            solicitud.setObservacionesVisitaTecnica(getVariableValue(variablesMap, "observacionesVisitaTecnica"));

            solicitud.setCodigoSolicitud(idProceso);
            return solicitud;
        } else {
            return null;
        }
    }

    private String getVariableValue(Map<String, Object> map, String variableName) {
        if (map.containsKey(variableName)) {
            Object variable = map.get(variableName);
            if (variable instanceof Map<?, ?> valueMap && valueMap.get("value") != null) {
                return valueMap.get("value").toString();
            }
        }
        return null;
    }


    public void updateTaskByProcessId(String processId, String taskId) {
        for (TaskInfo taskInfo : tasksList) {
            if (taskInfo.getProcessId().equals(processId)) {
                taskInfo.setTaskId(taskId);
            }
        }
    }

    @Override
    public List<SolicitudPreviewDTO> obtenerSolicitudesVistaPrevia() {
        String apiUrl = camundaUrl + "history/process-instance?processDefinitionKey=licenciamientoAmbientalProcess&sortBy=startTime&sortOrder=desc";

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<SolicitudPreviewDTO> lista = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (JsonNode instancia : response.getBody()) {
                String processInstanceId = instancia.get("id").asText();

                try {
                    SolicitudDTO solicitud = getProcessVariablesById(processInstanceId);
                    if (solicitud != null && !"BORRADOR".equalsIgnoreCase(solicitud.getEstado())) {
                        SolicitudPreviewDTO preview = new SolicitudPreviewDTO();
                        preview.setCodigoSolicitud(solicitud.getCodigoSolicitud());
                        preview.setNombreSolicitante(solicitud.getNombreSolicitante());
                        preview.setNombreProyecto(solicitud.getNombreProyecto());
                        preview.setEstado(solicitud.getEstado());
                        preview.setProfesionalAsignado(solicitud.getProfesionalAsignado());

                        preview.setFechaVisitaTecnica(solicitud.getFechaVisitaTecnica());
                        preview.setObservacionesVisitaTecnica(solicitud.getObservacionesVisitaTecnica());

                        lista.add(preview);
                    }
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    System.err.println("No se pudo obtener variables del proceso con ID: " + processInstanceId);
                    System.err.println("Detalles: " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Error inesperado al obtener variables de proceso: " + ex.getMessage());
                }
            }
        }

        return lista;
    }

    @Override
    public void realizarVisitaTecnica(String processId, VisitaTecnicaDTO visitaDTO, String nuevoEstado) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> variables = new HashMap<>();
            variables.put("visita", Map.of("value", visitaDTO.isVisitaRealizada(), "type", "Boolean"));
            variables.put("fechaVisitaTecnica", Map.of("value", visitaDTO.getFechaVisitaTecnica(), "type", "String"));
            variables.put("profesionalAsignado", Map.of("value", visitaDTO.getProfesionalAsignado(), "type", "String"));
            variables.put("coherenciaImpactoEIA", Map.of("value", visitaDTO.isCoherenciaImpactoEIA(), "type", "Boolean"));
            variables.put("observacionesVisitaTecnica", Map.of("value", visitaDTO.getObservacionesAdicionalesVisita(), "type", "String"));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("variables", variables);

            try {
                String apiRequestUrl = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(apiRequestUrl, new HttpEntity<>(requestBody, headers), Map.class);

                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);

                    setAssignee(newTaskId, "ProfesionalTecnico");
                    actualizarEstadoService.actualizarEstadoDatabase(processId, nuevoEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, nuevoEstado);
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Error durante la finalización de la tarea: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("No se encontró la tarea para el proceso " + processId);
        }
    }

    @Override
    public void emitirConceptoTecnico(String processId, String fechaConceptoTecnico, String nuevoEstado) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> variables = new HashMap<>();
            variables.put("fechaConceptoTecnico", Map.of("value", fechaConceptoTecnico, "type", "String"));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("variables", variables);

            try {
                String apiRequestUrl = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(apiRequestUrl, new HttpEntity<>(requestBody, headers), Map.class);

                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);

                    setAssignee(newTaskId, "CoordinadorDeGrupo");
                    actualizarEstadoService.actualizarEstadoDatabase(processId, nuevoEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, nuevoEstado);
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Error durante la finalización de la tarea: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("No se encontró la tarea para el proceso " + processId);
        }
    }

}
