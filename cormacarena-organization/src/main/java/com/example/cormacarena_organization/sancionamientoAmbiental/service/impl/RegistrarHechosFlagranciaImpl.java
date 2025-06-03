package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaProcesoVerificacionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RegistrarHechosFlagrancia;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseProcessServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Service
public class RegistrarHechosFlagranciaImpl extends BaseProcessServiceImpl implements RegistrarHechosFlagrancia {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;
    @Override
    public DenunciaProcesoVerificacionDTO getProcessVariablesById(String processId) {
        String URL= camundaURL + "process-instance/" + processId + "/variables?deserializeValues=true";
        SancionamientoAmbiental sancionamientoAmbiental =sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);


        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> variablesMap = responseEntity.getBody();
        if (variablesMap != null) {
            DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO =new DenunciaProcesoVerificacionDTO();
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            denunciaProcesoVerificacionDTO.setDescripcionDenuncia(descripcionDenuncia);

            Map<String, Object>  numeroDocumentoDenucianteMap = (Map<String , Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroDocumentoDenuciante = ((Integer) numeroDocumentoDenucianteMap.get("value")).longValue();

            denunciaProcesoVerificacionDTO.setNumeroDocumentoDenuciante(numeroDocumentoDenuciante);
            denunciaProcesoVerificacionDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());

            return denunciaProcesoVerificacionDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(DenunciaProcesoVerificacionDTO verificacionHechoDTO, String processId) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("descripcionVisitaTecnica", Map.of("value", verificacionHechoDTO.getDescripcionVisitaTecnica(), "type", "String"));
        variables.put("encargadoVisitaTecnica",Map.of("value",verificacionHechoDTO.getEncargadoVisitaTecnica(),"type","String"));
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
