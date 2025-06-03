package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ActoAdministrativaDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.ResolucionActoAdministrativo;
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
public class ResolucionActoAdministrativoImpl extends BaseProcessServiceImpl implements ResolucionActoAdministrativo {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;
    @Override
    public ActoAdministrativaDTO getProcessVariablesById(String processId) {
        String childProcessId = getChildProcessInstanceId(processId);
        String URL= camundaURL + "process-instance/" + childProcessId + "/variables?deserializeValues=true";
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
            ActoAdministrativaDTO actoAdministrativaDTO =new ActoAdministrativaDTO();

            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            actoAdministrativaDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> profesionalActoAdministrativoMap = (Map<String, Object>) variablesMap.get("profesionalActoAdministrativo");
            String profesionalActoAdministrativo = (String) profesionalActoAdministrativoMap.get("value");
            actoAdministrativaDTO.setProfesionalActoAdministrativo(profesionalActoAdministrativo);
            Map<String, Object> descripcionActoAdministrativoMap = (Map<String, Object>) variablesMap.get("descripcionActoAdministrativo");
            String descripcionActoAdministrativo = (String) descripcionActoAdministrativoMap.get("value");
            actoAdministrativaDTO.setDescripcionActoAdministrativo(descripcionActoAdministrativo);
            actoAdministrativaDTO.setIdDenuncia(sancionamientoAmbiental.getId());


            return actoAdministrativaDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(ActoAdministrativaDTO actoAdministrativaDTO, String processId) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String fecha = formato.format(actoAdministrativaDTO.getFechaActoAdministrativo());
        System.out.println(fecha);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("fechaActoAdministrativo", Map.of("value", fecha, "type", "String"));
        variables.put("tipoResolucion", Map.of("value", actoAdministrativaDTO.getTipoResolucion(), "type", "String"));
        variables.put("DescripcionResolucion", Map.of("value", actoAdministrativaDTO.getDescripcionResolucion(), "type", "String"));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);

        String taskId = getTaskIdByProcessIdWithApi(processId);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(camundaURL+"/task/"+taskId+"/submit-form", requestEntity,Map.class);

            return "Fine";
        }catch (HttpClientErrorException e){
            return  e.getResponseBodyAsString();
        }
    }
}
