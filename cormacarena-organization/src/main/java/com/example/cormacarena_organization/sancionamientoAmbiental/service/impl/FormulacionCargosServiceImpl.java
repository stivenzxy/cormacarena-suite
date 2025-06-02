package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.FormulacionCargosDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.FormulacionCargosService;
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
public class FormulacionCargosServiceImpl extends BaseProcessServiceImpl implements FormulacionCargosService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public FormulacionCargosDTO getProcessVariablesById(String processId) {
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
            FormulacionCargosDTO formulacionCargosDTO =new FormulacionCargosDTO();
            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            formulacionCargosDTO.setDescripcionDenuncia(descripcionDenuncia);
            Map<String, Object> numeroIdentificacionDenuncianteMap = (Map<String, Object>) variablesMap.get("numeroIdentificacionDenunciante");
            Long numeroIdentificacionDenunciante = ((Integer) numeroIdentificacionDenuncianteMap.get("value")).longValue();
            formulacionCargosDTO.setNumeroDocumentoDenuciante(numeroIdentificacionDenunciante);
            formulacionCargosDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());


            return formulacionCargosDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(FormulacionCargosDTO formulacionCargosDTO, String processId) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String fecha = formato.format(formulacionCargosDTO.getFechaFormulacionCargos());
        System.out.println(fecha);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("fechaFormulacionCargos", Map.of("value", fecha, "type", "String"));
        variables.put("nombreInfractor", Map.of("value", formulacionCargosDTO.getNombreInfractor(), "type", "String"));
        variables.put("DescripcionFormulacionCargos", Map.of("value", formulacionCargosDTO.getDescripcionFormulacionCargos(), "type", "String"));
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
