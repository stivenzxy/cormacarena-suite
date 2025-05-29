package com.example.cormacarena_client.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.repository.SolicitudRepository;
import com.example.cormacarena_client.licenciamientoAmbiental.service.ActualizacionEstadoProceso;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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

    private List<TaskInfo> tasksList = new ArrayList<>();

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
    public String actualizarVariablesProceso(String idProceso , SolicitudLicencia solicitudLicencia) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> modificaciones = new HashMap<>();
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

        System.out.println(modificaciones);
        System.out.println(idProceso);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("modifications", modificaciones);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(camundaUrl+"process-instance/"+idProceso+"/variables",
                    HttpMethod.POST, entity, String.class);

            System.out.println("Variables actualizadas correctamente: " + response.getBody());
            return String.valueOf(solicitudLicencia.getIdSolicitante());
        } catch (Exception e) {
            System.err.println("Error al actualizar las variables del proceso: " + e.getMessage());
        }
        return "";
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
    public String completeTask(String idProceso) {
      TaskInfo taskInfo = getTaskInfoByProcessId(idProceso);

      if (taskInfo != null) {
          String taskId = taskInfo.getTaskId();

          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);

          Map<String, Object> requestBody = new HashMap<>();
          HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

          try {
              ResponseEntity<Map> response = restTemplate.postForEntity(camundaUrl+"task/"+taskId+"/complete", entity, Map.class);
              TaskInfo taskInfoDetails = getTaskInfoByProcessId(idProceso);
              setAssignee(taskInfoDetails.getTaskId(), "CoordinadorDeGrupo");
              actualizacionEstado.updateReviewAndStatus(idProceso,"Verificar datos de la solicitud");
              SolicitudLicencia solicitudLicencia = solicitudRepository.findByCodigoSolicitud(idProceso);
              System.out.println(taskInfoDetails.getTaskName());
              solicitudLicencia.setEstado(taskInfoDetails.getTaskName());
              solicitudLicenciaService.actualizarSolicitudExistente(solicitudLicencia.getCodigoSolicitud(), solicitudLicencia);
              return String.valueOf(solicitudLicencia.getIdSolicitante());
          } catch (HttpClientErrorException e) {
              String errorMessage = e.getResponseBodyAsString();
              System.err.println("Error with Camunda request: " + errorMessage);
              return null;
          }
      } else {
          System.err.println("Could not get task information for process ID: " + idProceso);
          return null;
      }
    }
}
