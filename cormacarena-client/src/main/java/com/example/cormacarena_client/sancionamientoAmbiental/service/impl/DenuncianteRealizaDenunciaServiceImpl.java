package com.example.cormacarena_client.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_client.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteRealizaDenunciaService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DenuncianteRealizaDenunciaServiceImpl implements DenuncianteRealizaDenunciaService {

    private final RestTemplate restTemplate;
    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUser;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    private List<TaskInfo> taskList = new ArrayList<>();


    @Override
    public String startProcessInstance(DenunciaDTO denunciaDTO, String soporte) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombresDenunciante", Map.of("value", denunciaDTO.getNombresDenunciante(), "type", "String"));
        variables.put("apellidosDenunciante", Map.of("value", denunciaDTO.getApellidosDenunciante(), "type", "String"));
        variables.put("tipoIdentificacionDenunciante", Map.of("value", denunciaDTO.getTipoIdentificacionDenunciante(), "type", "String"));
        variables.put("numeroIdentificacionDenunciante", Map.of("value", denunciaDTO.getNumeroIdentificacionDenunciante(), "type", "Long"));
        variables.put("correoDenunciante", Map.of("value",denunciaDTO.getCorreoDenunciante(),"type","String"));
        variables.put("descripcionDenuncia",Map.of("value",denunciaDTO.getDescripcionDenuncia(),"type","String"));
        variables.put("soporteDenuncia",Map.of("value",soporte,"type","String"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(camundaUrl+"process-definition/key/SancionamientoAmbiental/submit-form", requestEntity,Map.class);
            String processId = String.valueOf(response.getBody().get("id"));
            TaskInfo taskInfo = getTaskInfoByProcessIdWithApi(processId);
            setAssignee(taskInfo.getTaskId(),"Denunciante");
            taskInfo.setProcessId(processId);
            return processId;
        }catch (HttpClientErrorException e){
            return  e.getResponseBodyAsString();
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
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl+"task/"+taskId+"/assignee", HttpMethod.POST, requestEntity,String.class);
            System.out.println("Assignee set successfully");
        } catch (HttpClientErrorException e){
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error in the Camunda request: "+ errorMessage);
        }

    }

    @Override
    public TaskInfo getTaskInfoByProcessId(String processId) {
        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl+"task?processInstanceId="+processId,HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>(){
            });
            List<Map> tasks = response.getBody();
            if (tasks != null && !tasks.isEmpty()) {
                Map<String, String> taskInfoMap = new HashMap<>();
                taskInfoMap.put("taskId", String.valueOf(tasks.get(0).get("id")));
                taskInfoMap.put("taskName", String.valueOf(tasks.get(0).get("name")));
                taskInfoMap.put("assignee", String.valueOf(tasks.get(0).get("assignee")));

                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setProcessId(processId);
                taskInfo.setTaskId(taskInfoMap.get("taskId"));
                taskInfo.setTaskName(taskInfoMap.get("taskName"));
                taskInfo.setTaskAssignee(taskInfoMap.get("assignee"));

                taskList.add(taskInfo);
                return taskInfo;
        }else {
                System.err.println("No tasks found for Process ID " + processId);
                return null;
            }
    }catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error with Camunda request: " + errorMessage);
            return null;
        }
    }

    @Override
    public TaskInfo getTaskInfoByProcessIdWithApi(String processId) {
        try {
            ResponseEntity<List<Map>> response = restTemplate.exchange(camundaUrl+"task?processInstanceId="+processId, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>() {
            });
            List<Map> tasks = response.getBody();

            if (tasks != null && !tasks.isEmpty()) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskId(String.valueOf(tasks.get(0).get("id")));
                taskInfo.setTaskName(String.valueOf(tasks.get(0).get("name")));
                taskInfo.setTaskAssignee(String.valueOf(tasks.get(0).get("assignee")));

                return taskInfo;
            } else {
                System.err.println("No tasks found for Process ID " + processId);
                return null;
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error with Camunda request: " + errorMessage);
            return null;
        }
    }

    @Override
    public String updateProcessVariables(String processId, DenunciaDTO denunciaDTO, String ruta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> modifications = new HashMap<>();
        modifications.put("nombresDenunciante", Map.of("value", denunciaDTO.getNombresDenunciante(), "type", "String"));
        modifications.put("apellidosDenunciante", Map.of("value", denunciaDTO.getApellidosDenunciante(), "type", "String"));
        modifications.put("tipoIdentificacionDenunciante", Map.of("value", denunciaDTO.getTipoIdentificacionDenunciante(), "type", "String"));
        modifications.put("numeroIdentificacion", Map.of("value", denunciaDTO.getNumeroIdentificacionDenunciante(), "type", "Long"));
        modifications.put("correoDenunciante", Map.of("value",denunciaDTO.getCorreoDenunciante(),"type","String"));
        modifications.put("descripcionDenuncia",Map.of("value",denunciaDTO.getDescripcionDenuncia(),"type","String"));
        modifications.put("soporteDenuncia",Map.of("value",ruta,"type","String"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("modifications", modifications);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl+"process-instance/"+processId+"/variables", HttpMethod.PUT, requestEntity, String.class);
            System.out.println("Variables updated successfully: " + response.getBody());
            return String.valueOf(denunciaDTO.getNumeroIdentificacionDenunciante());
        } catch (Exception e) {
            System.err.println("Error while updating variables: " + e.getMessage());
        }
        return "";

    }

    @Override
    public String completeTask(String processId,DenunciaDTO denunciaDTO) {
        System.out.println("PROCESS id: "+ processId);
        TaskInfo taskInfo = getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nombresDenunciante", Map.of("value", denunciaDTO.getNombresDenunciante(), "type", "String"));
            requestBody.put("apellidosDenunciante", Map.of("value", denunciaDTO.getApellidosDenunciante(), "type", "String"));
            requestBody.put("tipoIdentificacionDenunciante", Map.of("value", denunciaDTO.getTipoIdentificacionDenunciante(), "type", "String"));
            requestBody.put("numeroIdentificacion", Map.of("value", denunciaDTO.getNumeroIdentificacionDenunciante(), "type", "Long"));
            requestBody.put("correoDenunciante", Map.of("value",denunciaDTO.getCorreoDenunciante(),"type","String"));
            requestBody.put("descripcionDenuncia",Map.of("value",denunciaDTO.getDescripcionDenuncia(),"type","String"));
            requestBody.put("soporteDenuncia",Map.of("value",denunciaDTO.getNombresDenunciante(),"type","String"));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
//                ResponseEntity<Map> response = restTemplate.postForEntity("http://bpmengine:9000/engine-rest"+"/task/"+taskId+"/complete", requestEntity, Map.class);
                ResponseEntity<Map> response = restTemplate.postForEntity(camundaUrl+"task/"+taskId+"/complete", requestEntity, Map.class);
                TaskInfo taskInfo1 = getTaskInfoByProcessIdWithApi(processId);
                setAssignee(taskInfo1.getTaskId(), "subdirecciónAutoridadAmbiental");
                updateReviewAndStatus(processId,"Radicar la denuncia");
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                sancionamientoAmbiental.setStatus(taskInfo1.getTaskName());
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(), sancionamientoAmbiental);
                return String.valueOf(sancionamientoAmbiental.getDenuncia().getIdDenuncia());
            } catch (HttpClientErrorException e) {
                String errorMessage = e.getResponseBodyAsString();
                System.err.println("Error with Camunda request: " + errorMessage);
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Could not get task information for process ID: " + processId);
            return null;
        }
    }

    @Override
    public void messageEvent(String processId) {
        String messageName = "hayIncosistencias";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("inconsistenciasSubsanadas", Map.of("value", true, "type", "boolean"));

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("messageName", messageName);
        requestBodyMap.put("businessKey", processId);
        requestBodyMap.put("processVariables", processVariables);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (JsonProcessingException e) {
            System.err.println("error converting request body to JSON");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(camundaUrl+"/message", HttpMethod.POST, requestEntity, String.class);
            System.out.println("Message event done. BusinessID: "+processId);
            updateReviewAndStatus(processId,"Revisar detalles de solicitud");
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            System.err.println("Error with Camunda request: " + errorMessage);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void updateReviewAndStatus(String processId, String status) throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);


        String updateQuery = "UPDATE sancionamiento_ambiental SET status = ? WHERE process_id = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, status);
            updateStatement.setString(2, processId);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Status updated, and count_reviewcr incremented.");
            } else {
                System.out.println("No records found for the given processId: " + processId);
            }
        }
    }
}
