package com.example.cormacarena_organization.licenciamientoAmbiental.service.Impl;


import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.licenciamientoAmbiental.enums.SectorProyecto;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ActualizarEstadoService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.CoordinadorRevSolService;
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
public class CoordinadorRevSolServiceImpl implements CoordinadorRevSolService {

    private final RestTemplate restTemplate;
    private List<TaskInfo> tasksList = new ArrayList<>();
    private final ActualizarEstadoService actualizarEstadoService;

    @Value("${camunda.url}")
    private String camundaUrl;

    @Override
    @SuppressWarnings("unchecked")
    public SolicitudDTO getProcessVariablesById(String idProceso) {
        String apiRequestUrl = camundaUrl + "process-instance/" + idProceso + "/variables?deserializeValues=true";

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                apiRequestUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> variablesMap = responseEntity.getBody();

        if (variablesMap != null) {
            SolicitudDTO solicitud = new SolicitudDTO();

            solicitud.setNombreSolicitante(getVariableValue(variablesMap, "nombreSolicitante", String.class));
            solicitud.setTipoIdentificacion(getVariableValue(variablesMap, "tipoIdentificacion", String.class));
            solicitud.setIdSolicitante(getVariableValue(variablesMap, "idSolicitante", String.class));
            solicitud.setTelefono(getVariableValue(variablesMap, "telefono", String.class));
            solicitud.setEmail(getVariableValue(variablesMap, "email", String.class));
            solicitud.setDireccionResidencia(getVariableValue(variablesMap, "direccionResidencia", String.class));
            solicitud.setNombreProyecto(getVariableValue(variablesMap, "nombreProyecto", String.class));

            String sectorStr = getVariableValue(variablesMap, "sectorProyecto", String.class);
            if (sectorStr != null) {
                solicitud.setSectorProyecto(SectorProyecto.valueOf(sectorStr));
            }

            Long valorProyecto = getVariableValue(variablesMap, "valorProyecto", Long.class);
            if (valorProyecto != null) {
                solicitud.setValorProyecto(valorProyecto);
            }

            solicitud.setDepartamentoProyecto(getVariableValue(variablesMap, "departamentoProyecto", String.class));
            solicitud.setMunicipioProyecto(getVariableValue(variablesMap, "municipioProyecto", String.class));
            solicitud.setNombreSoporteEIA(getVariableValue(variablesMap, "nombreSoporteEIA", String.class));
            solicitud.setEstado(getVariableValue(variablesMap, "estado", String.class));
            solicitud.setFechaConceptoTecnico(getVariableValue(variablesMap, "fechaConceptoTecnico", String.class));
            solicitud.setObservacionesVisitaTecnica(getVariableValue(variablesMap, "observacionesVisitaTecnica", String.class));
            solicitud.setProfesionalAsignado(getVariableValue(variablesMap, "profesionalAsignado", String.class));
            solicitud.setFechaResolucionJuridica(getVariableValue(variablesMap, "fechaResolucionJuridica", String.class));
            solicitud.setDescripcionResolucionJuridica(getVariableValue(variablesMap, "descripcionResolucionJuridica", String.class));

            solicitud.setCodigoSolicitud(idProceso);
            return solicitud;
        } else {
            return null;
        }
    }

    private <T> T getVariableValue(Map<String, Object> map, String variableName, Class<T> type) {
        if (map.containsKey(variableName)) {
            Object variable = map.get(variableName);
            if (variable instanceof Map<?, ?> valueMap && valueMap.get("value") != null) {
                Object value = valueMap.get("value");
                if (type.isInstance(value)) {
                    return type.cast(value);
                } else if (type == Long.class && value instanceof Number) {
                    return type.cast(((Number) value).longValue());
                } else if (type == String.class) {
                    return type.cast(value.toString());
                }
            }
        }
        return null;
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
            String mainTaskUrl = camundaUrl + "task?processInstanceId=" + idProceso;
            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    mainTaskUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {}
            );
            List<Map> tasks = response.getBody();

            if (tasks != null && !tasks.isEmpty()) {
                return String.valueOf(tasks.getFirst().get("id"));
            }

            String subProcessUrl = camundaUrl + "process-instance?superProcessInstance=" + idProceso;
            ResponseEntity<List<Map>> subProcessResponse = restTemplate.exchange(
                    subProcessUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {}
            );
            List<Map> subProcesses = subProcessResponse.getBody();

            if (subProcesses != null && !subProcesses.isEmpty()) {
                String subProcessId = String.valueOf(subProcesses.getFirst().get("id"));

                String subTaskUrl = camundaUrl + "task?processInstanceId=" + subProcessId;
                ResponseEntity<List<Map>> subTaskResponse = restTemplate.exchange(
                        subTaskUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {}
                );
                List<Map> subTasks = subTaskResponse.getBody();

                if (subTasks != null && !subTasks.isEmpty()) {
                    return String.valueOf(subTasks.getFirst().get("id"));
                }
            }

            System.err.println("No se encontraron tareas para el proceso (ni para subprocesos): " + idProceso);
            return null;

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
                        preview.setProfesionalAsignado(solicitud.getProfesionalAsignado());
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
    public void updateTaskByProcessId(String processId, String taskId) {
        for (TaskInfo taskInfo : tasksList) {
            if (taskInfo.getProcessId().equals(processId)) {
                taskInfo.setTaskId(taskId);
            }
        }
    }

    @Override
    public void procesarSolicitud(String processId, String observacion, boolean esValida, String siguienteEstado, String nuevoResponsable) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> variables = new HashMap<>();

            variables.put("solicitudValida", Map.of(
                    "value", esValida,
                    "type", "Boolean"
            ));
            variables.put("observacionDatosIniciales", Map.of(
                    "value", observacion,
                    "type", "String"
            ));

            requestBody.put("variables", variables);

            try {
                String apiRequestUrl = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(apiRequestUrl, new HttpEntity<>(requestBody, headers), Map.class);

                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);
                    setAssignee(newTaskId, nuevoResponsable);
                    actualizarEstadoService.actualizarEstadoDatabase(processId, siguienteEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, siguienteEstado);
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Error during task completion: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("No task information found for Process ID " + processId);
        }
    }

    @Override
    public void asignarProfesional(String processId, String nombreProfesional, String siguienteEstado) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> variables = new HashMap<>();
            Map<String, Object> nombreProfesionalVar = new HashMap<>();

            nombreProfesionalVar.put("value", nombreProfesional);
            nombreProfesionalVar.put("type", "String");

            variables.put("profesionalAsignado", nombreProfesionalVar);
            requestBody.put("variables", variables);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                String url = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(url, requestEntity, Map.class);

                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);
                    setAssignee(newTaskId, "ProfesionalTecnico");

                    actualizarEstadoService.actualizarEstadoDatabase(processId, siguienteEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, siguienteEstado);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el estado en la base de datos: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("Error al asignar el profesional: " + e.getMessage(), e);
            }

        } else {
            throw new RuntimeException("No se encontró la tarea activa para el proceso: " + processId);
        }
    }

    @Override
    public void aprobarConceptoTecnico(String processId, String siguienteEstado) {
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                String url = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(url, requestEntity, Map.class);

                String newTaskId = getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    updateTaskByProcessId(processId, newTaskId);
                    setAssignee(newTaskId, "OficinaJuridica");

                    actualizarEstadoService.actualizarEstadoDatabase(processId, siguienteEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, siguienteEstado);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el estado en la base de datos: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("Error al asignar el profesional: " + e.getMessage(), e);
            }

        } else {
            throw new RuntimeException("No se encontró la tarea activa para el proceso: " + processId);
        }
    }

}