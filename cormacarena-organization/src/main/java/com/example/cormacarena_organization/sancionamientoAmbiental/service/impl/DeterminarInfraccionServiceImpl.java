package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DeterminarInfraccionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.FormulacionCargosDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.DeterminarInfractorService;
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

@Service
@RequiredArgsConstructor
public class DeterminarInfractorServiceImpl extends BaseProcessServiceImpl implements DeterminarInfractorService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public DeterminarInfraccionDTO getProcessVariablesById(String processId) {
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
            DeterminarInfraccionDTO determinarInfraccionDTO =new DeterminarInfraccionDTO();
            Map<String, Object> nombreInfractorMap = (Map<String, Object>) variablesMap.get("nombreInfractor");
            String nombreInfractor = (String) nombreInfractorMap.get("value");
            determinarInfraccionDTO.setNombreInfractor(nombreInfractor);
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            determinarInfraccionDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> numeroIdentificacionDenuncianteMap = (Map<String, Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroIdentificacionDenunciante = ((Integer) numeroIdentificacionDenuncianteMap.get("value")).longValue();
            determinarInfraccionDTO.setNumeroDocumentoDenuciante(numeroIdentificacionDenunciante);
            determinarInfraccionDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());


            return determinarInfraccionDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(DeterminarInfraccionDTO determinarInfraccionDTO, String processId) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("tipoInfraccion", Map.of("value", determinarInfraccionDTO.getTipoInfraccion(), "type", "String"));
        variables.put("valorInfracionBase", Map.of("value", determinarInfraccionDTO.getValorInfracionBase(), "type", "long"));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);
        String taskId = getTaskIdByProcessIdWithApi(processId);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(camundaURL+"/task/"+taskId+"/submit-form", requestEntity,Map.class);
            String childProcessId = getChildProcessInstanceId(processId);
            TaskInfo taskInfo = getTaskInfoByProcessId(childProcessId);
            setAssignee(taskInfo.getTaskId(),"GrupoGEIMA");
            taskInfo.setProcessId(processId);
            return "Fine";
        }catch (HttpClientErrorException e){
            return  e.getResponseBodyAsString();
        }
    }
}
