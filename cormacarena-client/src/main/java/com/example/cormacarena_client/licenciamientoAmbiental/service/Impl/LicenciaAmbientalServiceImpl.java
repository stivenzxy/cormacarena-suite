package com.example.cormacarena_client.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_client.licenciamientoAmbiental.repository.SolicitudRepository;
import com.example.cormacarena_client.licenciamientoAmbiental.service.ActualizacionEstadoProceso;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SolicitudLicencia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LicenciaAmbientalServiceImpl implements LicenciaAmbientalService {
    private final RestTemplate restTemplate;

    private final SolicitudLicenciaService solicitudLicenciaService;
    private final ActualizacionEstadoProceso actualizacionEstado;
    private final SolicitudRepository solicitudRepository;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;

    private final List<TaskInfo> tasksList = new ArrayList<>();

    @Override
    public String iniciarInstanciaProceso(SolicitudDTO solicitudDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreSolicitante", Map.of("value", solicitudDTO.getNombreSolicitante(), "type", "String"));
        variables.put("tipoIdentificacion", Map.of("value", solicitudDTO.getTipoIdentificacion(), "type", "String"));
        variables.put("idSolicitante", Map.of("value", solicitudDTO.getIdSolicitante(), "type", "String"));
        variables.put("telefono", Map.of("value", solicitudDTO.getTelefono(), "type", "String"));
        variables.put("email", Map.of("value", solicitudDTO.getEmail(), "type", "String"));
        variables.put("direccionResidencia", Map.of("value", solicitudDTO.getDireccionResidencia(), "type", "String"));
        variables.put("nombreProyecto", Map.of("value", solicitudDTO.getNombreProyecto(), "type", "String"));
        variables.put("sectorProyecto", Map.of("value", solicitudDTO.getSectorProyecto().name(), "type", "String"));
        variables.put("valorProyecto", Map.of("value", solicitudDTO.getValorProyecto(), "type", "Long"));
        variables.put("departamentoProyecto", Map.of("value", solicitudDTO.getDepartamentoProyecto(), "type", "String"));
        variables.put("municipioProyecto", Map.of("value", solicitudDTO.getMunicipioProyecto(), "type", "String"));
        variables.put("nombreSoporteEIA", Map.of("value", solicitudDTO.getNombreSoporteEIA(), "type", "String"));
        variables.put("estado", Map.of("value", solicitudDTO.getEstado(), "type", "String"));

        String fechaFormateada = solicitudDTO.getFechaSolicitud().toString();
        variables.put("fechaSolicitud", Map.of("value", fechaFormateada, "type", "String"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    camundaUrl + "process-definition/key/licenciamientoAmbientalProcess/start",
                    entity, Map.class);

            return Objects.requireNonNull(response.getBody()).get("id").toString();
        } catch (Exception e) {
            System.err.println("Error al iniciar proceso: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void actualizarVariablesProceso(String idProceso , SolicitudLicencia solicitudLicencia) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> modificaciones = new HashMap<>();
        modificaciones.put("codigoSolicitud", Map.of("value", solicitudLicencia.getCodigoSolicitud(), "type", "String"));
        modificaciones.put("nombreSolicitante", Map.of("value", solicitudLicencia.getNombreSolicitante(), "type", "String"));
        modificaciones.put("tipoIdentificacion", Map.of("value", solicitudLicencia.getTipoIdentificacion(), "type", "String"));
        modificaciones.put("idSolicitante", Map.of("value", solicitudLicencia.getIdSolicitante(), "type", "String"));
        modificaciones.put("telefono", Map.of("value", solicitudLicencia.getTelefono(), "type", "String"));
        modificaciones.put("email", Map.of("value", solicitudLicencia.getEmail(), "type", "String"));
        modificaciones.put("direccionResidencia", Map.of("value", solicitudLicencia.getDireccionResidencia(), "type", "String"));
        modificaciones.put("nombreProyecto", Map.of("value", solicitudLicencia.getNombreProyecto(), "type", "String"));
        modificaciones.put("sectorProyecto", Map.of("value", solicitudLicencia.getSectorProyecto().name(), "type", "String"));
        modificaciones.put("valorProyecto", Map.of("value", solicitudLicencia.getValorProyecto(), "type", "Long"));
        modificaciones.put("departamentoProyecto", Map.of("value", solicitudLicencia.getDepartamentoProyecto(), "type", "String"));
        modificaciones.put("municipioProyecto", Map.of("value", solicitudLicencia.getMunicipioProyecto(), "type", "String"));
        modificaciones.put("nombreSoporteEIA", Map.of("value", solicitudLicencia.getNombreSoporteEIA(), "type", "String"));
        modificaciones.put("estado", Map.of("value", solicitudLicencia.getEstado(), "type", "String"));

        String fechaFormateada = solicitudLicencia.getFechaSolicitud().toString();
        modificaciones.put("fechaSolicitud", Map.of("value", fechaFormateada, "type", "String"));

        System.out.println(modificaciones);
        System.out.println(idProceso);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("modifications", modificaciones);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl+"process-instance/"+idProceso+"/variables",
                    HttpMethod.POST, entity, String.class);

            System.out.println("Variables actualizadas correctamente: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error al actualizar las variables del proceso: " + e.getMessage());
        }
    }

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
    public void completeTask(String idProceso, String assignee) {
        TaskInfo taskInfo = getTaskInfoByProcessId(idProceso);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                restTemplate.postForEntity(camundaUrl + "task/" + taskId + "/complete", entity, Map.class);

                TaskInfo taskInfoDetails = getTaskInfoByProcessId(idProceso);

                SolicitudLicencia solicitudLicencia = solicitudRepository.findByCodigoSolicitud(idProceso);

                if (taskInfoDetails != null) {
                    String usuarioAsignar = (assignee != null) ? assignee : "Usuario";
                    setAssignee(taskInfoDetails.getTaskId(), usuarioAsignar);
                    solicitudLicencia.setEstado(taskInfoDetails.getTaskName());
                } else {
                    System.out.println("No hay tarea activa en este momento para el proceso: " + idProceso);
                }

                actualizacionEstado.updateReviewAndStatus(idProceso, solicitudLicencia.getEstado());
                solicitudLicenciaService.actualizarSolicitudExistente(solicitudLicencia.getCodigoSolicitud(), solicitudLicencia);

            } catch (HttpClientErrorException e) {
                String errorMessage = e.getResponseBodyAsString();
                System.err.println("Error with Camunda request: " + errorMessage);
            } catch (Exception e) {
                System.err.println("Error inesperado al completar tarea: " + e.getMessage());
            }
        } else {
            System.err.println("Could not get task information for process ID: " + idProceso);
        }
    }

    public Object getVariableProceso(String idProceso, String variableName) {
        String url = camundaUrl + "process-instance/" + idProceso + "/variables/" + variableName;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> variableMap = response.getBody();
                return variableMap.get("value");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener variable del proceso: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setProcessVariable(String processInstanceId, String variableName, Object value) {
        String url = camundaUrl + "process-instance/" + processInstanceId + "/variables/" + variableName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String camundaType = detectCamundaType(value);

        if (camundaType == null) {
            System.err.println("Tipo de dato no soportado para la variable: " + variableName);
            return;
        }

        Map<String, Object> valueInfo = new HashMap<>();
        valueInfo.put("value", value);
        valueInfo.put("type", camundaType);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(valueInfo, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
            System.out.println("Variable '" + variableName + "' seteada como '" + value + "' (tipo: " + camundaType + ") para proceso " + processInstanceId);
        } catch (HttpClientErrorException e) {
            System.err.println("Error al setear variable: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private String detectCamundaType(Object value) {
        if (value instanceof String) {
            return "String";
        } else if (value instanceof Boolean) {
            return "Boolean";
        } else if (value instanceof Integer) {
            return "Integer";
        } else if (value instanceof Double || value instanceof Float) {
            return "Double";
        } else if (value instanceof java.util.Date) {
            return "Date";
        } else {
            return null;
        }
    }
}