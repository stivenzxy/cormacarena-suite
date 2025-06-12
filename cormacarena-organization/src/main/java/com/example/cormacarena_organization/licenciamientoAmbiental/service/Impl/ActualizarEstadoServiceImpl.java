package com.example.cormacarena_organization.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_organization.licenciamientoAmbiental.service.ActualizarEstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActualizarEstadoServiceImpl implements ActualizarEstadoService {

    private final RestTemplate restTemplate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUser;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${camunda.url:http://localhost:8080/engine-rest/}")
    private String camundaUrl;

    @Override
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


    @Override
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
