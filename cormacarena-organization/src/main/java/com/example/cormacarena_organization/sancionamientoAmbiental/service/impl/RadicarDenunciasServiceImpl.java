package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaRadicaDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseProcessServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RadicarDenunciasServiceImpl extends BaseProcessServiceImpl implements RadicarDenunciasService {


    private final SancionamientoAmbientalService sancionamientoAmbientalService;


    @Override
    public DenunciaInfoRadicado getProcessVariablesById(String processId) {
        String URL= camundaURL + "process-instance/" + processId + "/variables?deserializeValues=true";

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> variablesMap = responseEntity.getBody();
        if (variablesMap != null) {
            DenunciaInfoRadicado denunciaInfoRadicado =new DenunciaInfoRadicado();
            Map<String, Object> nombresDenuncianteMap = (Map<String, Object>) variablesMap.get("nombresDenunciante");
            String nombresDenunciante = (String) nombresDenuncianteMap.get("value");
            denunciaInfoRadicado.setNombresDenunciante(nombresDenunciante);

            Map<String, Object> apellidosDenuncianteMap = (Map<String, Object>) variablesMap.get("apellidosDenunciante");
            String apellidosDenunciante = (String) apellidosDenuncianteMap.get("value");
            denunciaInfoRadicado.setApellidosDenunciante(apellidosDenunciante);

            Map<String, Object> correoDenuncianteMap = (Map<String, Object>) variablesMap.get("correoDenunciante");
            String correoDenunciante = (String) correoDenuncianteMap.get("value");
            denunciaInfoRadicado.setCorreoDenunciante(correoDenunciante);

            Map<String, Object> descripcionDenuncianteMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenunciante = (String) descripcionDenuncianteMap.get("value");
            denunciaInfoRadicado.setDescripcionDenuncia(descripcionDenunciante);
            return denunciaInfoRadicado;
        }else {
            return null;
        }
    }



    public String enviarFormulario(DenunciaRadicaDTO denunciaRadicaDTO, String processId) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fecha = formato.format(denunciaRadicaDTO.getFechaRadicacion());
        HttpHeaders headers = new HttpHeaders();
        DenunciaInfoRadicado denunciaInfoRadicado = getProcessVariablesById(processId);
        System.out.println(fecha);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombresDenunciante", Map.of("value", denunciaInfoRadicado.getNombresDenunciante(), "type", "String"));
        variables.put("apellidosDenunciante", Map.of("value", denunciaInfoRadicado.getApellidosDenunciante(), "type", "String"));
        variables.put("correoDenunciante", Map.of("value",denunciaInfoRadicado.getCorreoDenunciante(),"type","String"));
        variables.put("descripcionDenuncia",Map.of("value",denunciaInfoRadicado.getDescripcionDenuncia(),"type","String"));
        variables.put("fechaRadicacion",Map.of("value",fecha,"type","String"));
        variables.put("encargadoDenuncia",Map.of("value",denunciaRadicaDTO.getEncargadoDenuncia(),"type","String"));
        variables.put("flagrancia",Map.of("value",denunciaRadicaDTO.getFlagrancia(),"type","String"));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);
        String taskId = getTaskIdByProcessIdWithApi(processId);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(camundaURL+"/task/"+taskId+"/submit-form", requestEntity,Map.class);
            TaskInfo taskInfo = getTaskInfoByProcessId(processId);
            setAssignee(taskInfo.getTaskId(),"GrupoGEMA");
            taskInfo.setProcessId(processId);
            return "Fine";
        }catch (HttpClientErrorException e){
            return  e.getResponseBodyAsString();
        }

    }
}