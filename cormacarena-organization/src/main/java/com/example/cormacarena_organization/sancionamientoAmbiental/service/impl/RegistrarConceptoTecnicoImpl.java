package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RegistrarConceptoTecnico;
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
public class RegistrarConceptoTecnicoImpl extends BaseProcessServiceImpl implements RegistrarConceptoTecnico {

   private final SancionamientoAmbientalService sancionamientoAmbientalService;
    @Override
    public ConceptoTecnicoDTO getProcessVariablesById(String processId) {
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
            ConceptoTecnicoDTO conceptoTecnicoDTO =new ConceptoTecnicoDTO();
            Map<String, Object> encargadoVisitaTecnicaMap = (Map<String, Object>) variablesMap.get("encargadoVisitaTecnica");
            String encargadoVisitaTecnica = (String) encargadoVisitaTecnicaMap.get("value");
            conceptoTecnicoDTO.setEncargadoVisitaTecnica(encargadoVisitaTecnica);

            Map<String, Object> descripcionVisitaTecnicaMap = (Map<String, Object>) variablesMap.get("descripcionVisitaTecnica");
            String descripcionVisitaTecnica = (String) descripcionVisitaTecnicaMap.get("value");
            conceptoTecnicoDTO.setDescripcionVisitaTecnica(descripcionVisitaTecnica);
            conceptoTecnicoDTO.setNumeroDocumentoDenuciante(sancionamientoAmbiental.getDenuncia().getDenunciante().getNumeroIdentificacion());
            conceptoTecnicoDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());
            conceptoTecnicoDTO.setDescripcionDenuncia(sancionamientoAmbiental.getDenuncia().getDescripcionDenuncia());

            return conceptoTecnicoDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(ConceptoTecnicoDTO conceptoTecnicoDTO, String processId) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String fecha = formato.format(conceptoTecnicoDTO.getFechaCreacionInformeTecnico());
        System.out.println(fecha);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("fechaConceptoTecnico", Map.of("value", fecha, "type", "String"));
        variables.put("razonesInfraccion", Map.of("value", conceptoTecnicoDTO.getRazonesInfraccion(), "type", "String"));
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
