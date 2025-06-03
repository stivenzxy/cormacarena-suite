package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoPruebasDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DeterminarInfraccionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.ConceptoTecnicoPruebaService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseProcessServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConceptoTecnicoPruebasServiceImpl extends BaseProcessServiceImpl implements ConceptoTecnicoPruebaService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public ConceptoTecnicoPruebasDTO getProcessVariablesById(String processId) {
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
            ConceptoTecnicoPruebasDTO conceptoTecnicoPruebasDTO =new ConceptoTecnicoPruebasDTO();
            Map<String, Object> nombreInfractorMap = (Map<String, Object>) variablesMap.get("nombreInfractor");
            String nombreInfractor = (String) nombreInfractorMap.get("value");
            conceptoTecnicoPruebasDTO.setNombreInfractor(nombreInfractor);
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            conceptoTecnicoPruebasDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> numeroIdentificacionDenuncianteMap = (Map<String, Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroIdentificacionDenunciante = ((Integer) numeroIdentificacionDenuncianteMap.get("value")).longValue();
            conceptoTecnicoPruebasDTO.setNumeroDocumentoDenuciante(numeroIdentificacionDenunciante);
            conceptoTecnicoPruebasDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());


            return conceptoTecnicoPruebasDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(ConceptoTecnicoPruebasDTO conceptoTecnicoPruebasDTO, String processId) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("culpable", Map.of("value", conceptoTecnicoPruebasDTO.getCulpable(), "type", "Boolean"));
        variables.put("descripcionConclusiones", Map.of("value", conceptoTecnicoPruebasDTO.getDescripcionConclusiones(), "type", "String"));
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
