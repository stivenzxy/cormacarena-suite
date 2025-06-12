package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ActoAdministrativaDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RegistrarActoAdministrativoService;
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
public class RegistrarActoAdministrativoServiceImpl extends BaseProcessServiceImpl implements RegistrarActoAdministrativoService {

    private final SancionamientoAmbientalService sancionamientoAmbientalService;
    @Override
    public ActoAdministrativaDTO getProcessVariablesById(String processId) {

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
            ActoAdministrativaDTO actoAdministrativaDTO =new ActoAdministrativaDTO();

            Map<String, Object> descripcionDenunciaMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenuncia = (String) descripcionDenunciaMap.get("value");
            actoAdministrativaDTO.setDescripcionDenuncia(descripcionDenuncia);
            actoAdministrativaDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());
            actoAdministrativaDTO.setDescripcionDenuncia(sancionamientoAmbiental.getDenuncia().getDescripcionDenuncia());
            actoAdministrativaDTO.setNumeroDocumentoDenuciante(sancionamientoAmbiental.getDenuncia().getDenunciante().getNumeroIdentificacion());
            return actoAdministrativaDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(ActoAdministrativaDTO actoAdministrativaDTO, String processId) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String fecha = formato.format(actoAdministrativaDTO.getFechaActoAdministrativo());
            System.out.println(fecha+"profesionalActo: "+actoAdministrativaDTO.getProfesionalActoAdministrativo());
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> variables = new HashMap<>();
            variables.put("fechaActoAdministrativo", Map.of("value", fecha, "type", "String"));
            variables.put("tipoActoAdministrativo", Map.of("value", actoAdministrativaDTO.getTipoActoAdministrativo(), "type", "String"));
            variables.put("profesionalActoAdministrativo", Map.of("value", actoAdministrativaDTO.getProfesionalActoAdministrativo(), "type", "String"));
            variables.put("descripcionActoAdministrativo", Map.of("value", actoAdministrativaDTO.getDescripcionActoAdministrativo(), "type", "String"));
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("variables", variables);

            String taskId = getTaskIdByProcessIdWithApi(processId);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(camundaURL+"/task/"+taskId+"/submit-form", requestEntity,Map.class);

                TaskInfo taskInfo = getTaskInfoByProcessId(processId);
                setAssignee(taskInfo.getTaskId(),"SubdireccionAutoridadAmbiental");
                taskInfo.setProcessId(processId);
                return "Fine";
            }catch (HttpClientErrorException e){
                return  e.getResponseBodyAsString();
            }
        }
}
