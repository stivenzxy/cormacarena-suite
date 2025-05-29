package com.example.cormacarena_organization.licenciamientoAmbiental.service.Impl;


import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.licenciamientoAmbiental.enums.EstadoProceso;
import com.example.cormacarena_organization.licenciamientoAmbiental.enums.SectorProyecto;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.CoordinadorRevSolService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoordinadorRevSolServiceImpl implements CoordinadorRevSolService {

    private final RestTemplate restTemplate;
    private List<TaskInfo> tasksList = new ArrayList<>();

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUser;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;


    @Override
    public List<String> obtenerProcesosPorIdActividad(String idActividad) {
        String apiRequestUrl = camundaUrl+"history/activity-instance?sortBy=startTime&sortOrder=desc&activityId="+idActividad+"&finished=false&unfinished=true&withoutTenantId=false";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiRequestUrl, String.class);

        List<String> procesosIds = new ArrayList<>();

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                for (JsonNode node : jsonNode) {
                    String processInstanceId = node.get("processInstanceId").asText();
                    procesosIds.add(processInstanceId);
                }
            } catch (IOException e) {
                System.err.println("Error al analizar la respuesta JSON: " + e.getMessage());
            }
        } else {
            System.err.println("Error al obtener las instancias de proceso: " + responseEntity.getStatusCode());
        }

        return procesosIds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SolicitudDTO getProcessVariablesById(String idProceso) {
        String apiRequestUrl = camundaUrl+"process-instance/"+idProceso+"/variables?deserializeValues=true";

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

            solicitud.setNombreSolicitante((String) ((Map<String, Object>) variablesMap.get("nombreSolicitante")).get("value"));
            solicitud.setTipoIdentificacion((String) ((Map<String, Object>) variablesMap.get("tipoIdentificacion")).get("value"));
            solicitud.setIdSolicitante((String) ((Map<String, Object>) variablesMap.get("idSolicitante")).get("value"));
            solicitud.setTelefono((String) ((Map<String, Object>) variablesMap.get("telefono")).get("value"));
            solicitud.setEmail((String) ((Map<String, Object>) variablesMap.get("email")).get("value"));
            solicitud.setDireccionResidencia((String) ((Map<String, Object>) variablesMap.get("direccionResidencia")).get("value"));
            solicitud.setNombreProyecto((String) ((Map<String, Object>) variablesMap.get("nombreProyecto")).get("value"));
            solicitud.setSectorProyecto(SectorProyecto.valueOf((String) ((Map<String, Object>) variablesMap.get("sectorProyecto")).get("value")));
            solicitud.setValorProyecto(((Number) ((Map<String, Object>) variablesMap.get("valorProyecto")).get("value")).longValue());
            solicitud.setDepartamentoProyecto((String) ((Map<String, Object>) variablesMap.get("departamentoProyecto")).get("value"));
            solicitud.setMunicipioProyecto((String) ((Map<String, Object>) variablesMap.get("municipioProyecto")).get("value"));
            solicitud.setNombreSoporteEIA((String) ((Map<String, Object>) variablesMap.get("nombreSoporteEIA")).get("value"));
            solicitud.setEstado((String) ((Map<String, Object>) variablesMap.get("estado")).get("value"));

            solicitud.setCodigoSolicitud(idProceso);
            return solicitud;
        } else {
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
    public TaskInfo getTaskInfoByProcessId(String idProceso) {
        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl+"task?processInstanceId="+idProceso, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            List<Map> tasks = response.getBody();

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

                tasksList.add(taskInfo);
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
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl+"task?processInstanceId="+idProceso, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {
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

    public void updateTaskByProcessId(String processId, String taskId) {
        for (TaskInfo taskInfo : tasksList) {
            if (taskInfo.getProcessId().equals(processId)) {
                taskInfo.setTaskId(taskId);
            }
        }
    }

    @Override
    public String approveTask(String processId) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> variables = new HashMap<>();
            Map<String, Object> solicitudValida = new HashMap<>();
            solicitudValida.put("value", true);
            solicitudValida.put("type", "Boolean");
            variables.put("solicitudValida", solicitudValida);
            requestBody.put("variables", variables);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                String apiRequestUrl = camundaUrl+"task/"+taskId+"/complete";

                restTemplate.postForEntity(apiRequestUrl, requestEntity, Map.class);
                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                System.out.println(newTaskId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);
                    setAssignee(newTaskId, "CoordinadorDeGrupo");
                    actualizarEstadoDatabase(processId, EstadoProceso.APROBADO.toString());
                    actualizarEstadoProceso(processId, EstadoProceso.APROBADO.toString());
                }
                return "";
            } catch (HttpClientErrorException e) {
                System.err.println("Error during task completion: " + e.getMessage());
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("No task information found for Process ID " + processId);
            return null;
        }
    }

    @Override
    public String rejectTask(String processId) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            System.out.println(taskId);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> variables = new HashMap<>();
            Map<String, Object> solicitudValida = new HashMap<>();
            solicitudValida.put("value", false);
            solicitudValida.put("type", "Boolean");
            variables.put("solicitudValida", solicitudValida);
            requestBody.put("variables", variables);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                String apiRequestUrl = camundaUrl + "task/" + taskId + "/complete";

                restTemplate.postForEntity(apiRequestUrl, requestEntity, Map.class);
                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                System.out.println("New Task ID: " + newTaskId);

                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);
                    setAssignee(newTaskId, "CoordinadorDeGrupo");
                    actualizarEstadoDatabase(processId, EstadoProceso.BORRADOR.toString());
                    actualizarEstadoProceso(processId, EstadoProceso.BORRADOR.toString());
                }
                return "";
            } catch (HttpClientErrorException e) {
                System.err.println("Error during task completion: " + e.getMessage());
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("No task information found for Process ID " + processId);
            return null;
        }
    }

    public void actualizarEstadoProceso(String processId, String estado) {
        String apiRequestUrl = camundaUrl + "process-instance/" + processId + "/variables/estado";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("value", estado);
        requestBody.put("type", "String");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    apiRequestUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Estado actualizado exitosamente para processId: " + processId);
            } else {
                System.err.println("Error actualizando estado para processId " + processId + ". Código: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error al actualizar estado: " + errorMessage);
        }
    }


    public void actualizarEstadoDatabase(String processId, String status) throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);

        String updateQuery = "UPDATE solicitud_licencia SET estado = ? WHERE codigo_solicitud = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, status);
            updateStatement.setString(2, processId);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Estado actualizado por coordinador de grupo" + status);
            } else {
                System.out.println("No records found for the given processId: " + processId);
            }
        }
    }
}