package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoPruebasDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.RecursosReposicionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.DeterminarRecursoReposicionService;
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
public class DeterminarRecursosReposicionServiceImpl extends BaseProcessServiceImpl implements DeterminarRecursoReposicionService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public RecursosReposicionDTO getProcessVariablesById(String processId) {
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
            RecursosReposicionDTO recursosReposicionDTO =new RecursosReposicionDTO();
            Map<String, Object> nombreInfractorMap = (Map<String, Object>) variablesMap.get("nombreInfractor");
            String nombreInfractor = (String) nombreInfractorMap.get("value");
            recursosReposicionDTO.setNombreInfractor(nombreInfractor);
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            recursosReposicionDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> numeroIdentificacionDenuncianteMap = (Map<String, Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroIdentificacionDenunciante = ((Integer) numeroIdentificacionDenuncianteMap.get("value")).longValue();
            recursosReposicionDTO.setNumeroDocumentoDenuciante(numeroIdentificacionDenunciante);
            recursosReposicionDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());


            return recursosReposicionDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(RecursosReposicionDTO recursosReposicionDTO, String processId) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("tipoRecursoReposicion", Map.of("value", recursosReposicionDTO.getTipoRecursoReposicion(), "type", "long"));
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
