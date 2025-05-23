package com.example.cormacarena_client.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LicenciaAmbientalServiceImpl implements LicenciaAmbientalService {
    private final RestTemplate restTemplate;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;

    @Override
    public String startProcessInstance(SolicitudDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Variables del proceso (ajusta los tipos según BPMN)
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreSolicitante", Map.of("value", dto.getNombreSolicitante(), "type", "String"));
        variables.put("tipoIdentificacion", Map.of("value", dto.getTipoIdentificacion(), "type", "String"));
        variables.put("idSolicitante", Map.of("value", dto.getIdSolicitante(), "type", "String"));
        variables.put("telefono", Map.of("value", dto.getTelefono(), "type", "String"));
        variables.put("email", Map.of("value", dto.getEmail(), "type", "String"));
        variables.put("direccionResidencia", Map.of("value", dto.getDireccionResidencia(), "type", "String"));
        variables.put("nombreProyecto", Map.of("value", dto.getNombreProyecto(), "type", "String"));
        variables.put("sectorProyecto", Map.of("value", dto.getSectorProyecto().name(), "type", "String"));
        variables.put("valorProyecto", Map.of("value", dto.getValorProyecto(), "type", "Long"));
        variables.put("departamentoProyecto", Map.of("value", dto.getDepartamentoProyecto(), "type", "String"));
        variables.put("municipioProyecto", Map.of("value", dto.getMunicipioProyecto(), "type", "String"));
        variables.put("estudioImpactoAmbiental", Map.of("value", dto.getDocumentoEIA(), "type", "String"));

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
}
