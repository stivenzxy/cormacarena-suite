package com.example.cormacarena_organization.sancionamientoAmbiental.service.base;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseProcessServiceImpl {


    protected final RestTemplate restTemplate = new RestTemplate();
    protected List<TaskInfo> tasksList = new ArrayList<>();

    @Value("${camunda.url}")
    protected String camundaURL;
    @Value("${spring.datasource.url}")
    protected String databaseUrl;
    @Value("${spring.datasource.username}")
    protected String databaseUser;
    @Value("${spring.datasource.password}")
    protected String databasePassword;
    public List<String> getAllProcessByActivityId(String activityId) {
        String url = camundaURL + "history/activity-instance?sortBy=startTime&sortOrder=desc&activityId=" + activityId + "&finished=false&unfinished=true&withoutTenantId=false";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        List<String> processIds = new ArrayList<>();

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                for (JsonNode node : jsonNode) {
                    String processInstanceId = node.get("processInstanceId").asText();
                    processIds.add(processInstanceId);
                }
            } catch (IOException e) {
                System.err.println("Error al analizar la respuesta JSON: " + e.getMessage());
            }
        } else {
            System.err.println("Error al obtener las instancias de proceso: " + responseEntity.getStatusCode());
        }

        return processIds;
    }

    public void setAssignee(String taskId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String camundaUrl = camundaURL + "task/" + taskId + "/assignee";

        try {
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl, HttpMethod.POST, requestEntity, String.class);
            System.out.println("Assignee set successfully");
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error in the Camunda request: " + errorMessage);
        }
    }

    public TaskInfo getTaskInfoByProcessId(String processId) {
        // Construir la URL para consultar las tareas relacionadas con el proceso
        String camundaUrl = camundaURL + "task?processInstanceId=" + processId;

        try {
            // Realizar una solicitud GET a Camunda para obtener la lista de tareas
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {
            });

            // Verificar si la respuesta contiene tareas
            List<Map> tasks = response.getBody();
            if (tasks != null && !tasks.isEmpty()) {
                // Supongamos que tomamos la primera tarea encontrada
                Map<String, String> taskInfoMap = new HashMap<>();
                taskInfoMap.put("taskId", String.valueOf(tasks.get(0).get("id")));
                taskInfoMap.put("taskName", String.valueOf(tasks.get(0).get("name")));
                taskInfoMap.put("assignee", String.valueOf(tasks.get(0).get("assignee")));

                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setProcessId(processId);
                taskInfo.setTaskId(taskInfoMap.get("taskId"));
                taskInfo.setTaskName(taskInfoMap.get("taskName"));
                taskInfo.setTaskAssignee(taskInfoMap.get("assignee"));

                tasksList.add(taskInfo);
                return taskInfo;
            } else {
                System.err.println("No tasks found for Process ID " + processId);
                return null;
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error en la solicitud a Camunda: " + errorMessage);
            return null;
        }
    }

    public String getChildProcessInstanceId(String parentProcessId) {
        String url = camundaURL + "/process-instance?superProcessInstance=" + parentProcessId;
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                // Asumimos el primero (puedes adaptar según tu lógica)
                return (String) body.get(0).get("id");
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error consultando procesos hijos: " + e.getResponseBodyAsString());
        }
        return null;
    }


    public String getTaskIdByProcessIdWithApi(String processId) {
        String camundaUrl = camundaURL + "task?processInstanceId=" + processId;

        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {
            });
            List<Map> tasks = response.getBody();

            if (tasks != null && !tasks.isEmpty()) {
                return String.valueOf(tasks.get(0).get("id"));
            } else {
                System.err.println("No tasks found for Process ID " + processId);
                return null;
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error en la solicitud a Camunda: " + errorMessage);
            return null;
        }
    }

    public String getTaskNameByProcessId(String processId) {
        for (TaskInfo taskInfo : tasksList) {
            if (taskInfo.getProcessId().equals(processId)) {
                return taskInfo.getTaskName();
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
    public String getParentProcessInstanceId(String childProcessInstanceId) {
        String url = camundaURL + "history/process-instance?processInstanceId=" + childProcessInstanceId;
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                Map<String, Object> firstItem = body.get(0);
                if (firstItem.containsKey("superProcessInstanceId")) {
                    return (String) firstItem.get("superProcessInstanceId");
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error consultando proceso padre: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error inesperado consultando proceso padre: " + e.getMessage());
        }
        return null;
    }

}

