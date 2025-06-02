package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoPruebasDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DisminuicionSancionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.DeterminarDisminuicionSancionService;
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
public class DeterminarDisminuicionSancionServiceImpl extends BaseProcessServiceImpl implements DeterminarDisminuicionSancionService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public DisminuicionSancionDTO getProcessVariablesById(String processId) {
        String URL = camundaURL + "process-instance/" + processId + "/variables?deserializeValues=true";
        SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> variablesMap = responseEntity.getBody();
        if (variablesMap != null) {
            DisminuicionSancionDTO disminuicionSancionDTO = new DisminuicionSancionDTO();
            Map<String, Object> nombreInfractorMap = (Map<String, Object>) variablesMap.get("nombreInfractor");
            String nombreInfractor = (String) nombreInfractorMap.get("value");
            disminuicionSancionDTO.setNombreInfractor(nombreInfractor);
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            disminuicionSancionDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> numeroIdentificacionDenuncianteMap = (Map<String, Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroIdentificacionDenunciante = ((Integer) numeroIdentificacionDenuncianteMap.get("value")).longValue();
            disminuicionSancionDTO.setNumeroDocumentoDenuciante(numeroIdentificacionDenunciante);
            disminuicionSancionDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());


            return disminuicionSancionDTO;
        } else {
            return null;
        }
    }
    @Override
    public String enviarFormulario(DisminuicionSancionDTO disminuicionSancionDTO, String processId) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("porcentajeDisminuicion", Map.of("value", disminuicionSancionDTO.getPorcentajeDisminuicion(), "type", "Long"));
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