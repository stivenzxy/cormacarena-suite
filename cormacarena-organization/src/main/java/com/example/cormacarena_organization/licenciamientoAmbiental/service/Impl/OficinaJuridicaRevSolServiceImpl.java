package com.example.cormacarena_organization.licenciamientoAmbiental.service.Impl;


import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ActualizarEstadoService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.OficinaJuridicaRevSolService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ProfesionalRevSolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class OficinaJuridicaRevSolServiceImpl implements OficinaJuridicaRevSolService {

    private final ProfesionalRevSolService profesionalRevSolService;

    private final RestTemplate restTemplate;
    private List<TaskInfo> tasksList = new ArrayList<>();
    private final ActualizarEstadoService actualizarEstadoService;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;

    @Override
    public void emitirResolucionAdministrativa(String processId, String fechaResolucionJuridica, String descripcionJuridica,String nuevoEstado) {
        TaskInfo taskInfo = profesionalRevSolService.getTaskInfoByProcessId(processId);

        if (taskInfo != null) {
            String taskId = taskInfo.getTaskId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();

            Map<String, Object> variables = new HashMap<>();

            variables.put("fechaResolucionJuridica", Map.of(
                    "value", fechaResolucionJuridica ,
                    "type", "String"
            ));
            variables.put("descripcionResolucionJuridica", Map.of(
                    "value", descripcionJuridica,
                    "type", "String"
            ));

            requestBody.put("variables", variables);

            try {
                String apiRequestUrl = camundaUrl + "task/" + taskId + "/complete";
                restTemplate.postForEntity(apiRequestUrl, new HttpEntity<>(requestBody, headers), Map.class);

                String newTaskId = profesionalRevSolService.getTaskIdByProcessIdWithApi(processId);
                if (newTaskId != null) {
                    profesionalRevSolService.updateTaskByProcessId(processId, newTaskId);
                    profesionalRevSolService.setAssignee(newTaskId, "DireccionGeneralDeLicenciamientoAmbiental");

                    actualizarEstadoService.actualizarEstadoDatabase(processId, nuevoEstado);
                    actualizarEstadoService.actualizarEstadoProceso(processId, nuevoEstado);
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
