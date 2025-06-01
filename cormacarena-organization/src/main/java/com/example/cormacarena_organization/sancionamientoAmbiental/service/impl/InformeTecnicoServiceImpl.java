package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.InformeTecnicoService;
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
public class InformeTecnicoServiceImpl extends BaseProcessServiceImpl implements InformeTecnicoService {
    private final SancionamientoAmbientalService sancionamientoAmbientalService;

    @Override
    public InformeTecnicoDTO getProcessVariablesById(String processId) {
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
            InformeTecnicoDTO informeTecnicoDTO =new InformeTecnicoDTO();
            Map<String, Object> encargadoVisitaTecnicaMap = (Map<String, Object>) variablesMap.get("encargadoVisitaTecnica");
            String encargadoVisitaTecnica = (String) encargadoVisitaTecnicaMap.get("value");
            informeTecnicoDTO.setEncargadoVisitaTecnica(encargadoVisitaTecnica);

            Map<String, Object> descripcionVisitaTecnicaMap = (Map<String, Object>) variablesMap.get("apellidosDenunciante");
            String descripcionVisitaTecnica = (String) descripcionVisitaTecnicaMap.get("value");
            informeTecnicoDTO.setDescripcionVisitaTecnica(descripcionVisitaTecnica);
            informeTecnicoDTO.setNumeroDocumentoDenuciante(sancionamientoAmbiental.getDenuncia().getDenunciante().getNumeroIdentificacion());
            informeTecnicoDTO.setIdDenuncia(sancionamientoAmbiental.getDenuncia().getIdDenuncia());
            informeTecnicoDTO.setDescripcionDenuncia(sancionamientoAmbiental.getDenuncia().getDescripcionDenuncia());

            return informeTecnicoDTO;
        }else {
            return null;
        }
    }

    @Override
    public String enviarFormulario(InformeTecnicoDTO informeTecnicoDTO, String processId) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String fecha = formato.format(informeTecnicoDTO.getFechaCreacionInformeTecnico());
        System.out.println(fecha);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> variables = new HashMap<>();
        variables.put("fechaCreacionInformeTecnico", Map.of("value", fecha, "type", "String"));
        variables.put("razonesNoCometerDelito", Map.of("value", informeTecnicoDTO.getRazonesNoCometerDelito(), "type", "String"));
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
