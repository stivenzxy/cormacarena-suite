package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseProcessServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RadicarDenunciasServiceImpl extends BaseProcessServiceImpl implements RadicarDenunciasService {



    @Override
    public List<DenunciaInfoRadicado> obtenerDenunicas() {
        String url = "http://localhost:9090/denuncia/denuncias";
        ResponseEntity<List<DenunciaInfoRadicado>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DenunciaInfoRadicado>>() {
                }
        );
        return response.getBody();
    }

    @Override
    public DenunciaInfoRadicado getProcessVariablesById(String processId) {
        String URL= camundaURL + "process-instance/" + processId + "/variables?deserializeValues=true";

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> variablesMap = responseEntity.getBody();
        if (variablesMap != null) {
            DenunciaInfoRadicado denunciaInfoRadicado =new DenunciaInfoRadicado();
            Map<String, Object> nombresDenuncianteMap = (Map<String, Object>) variablesMap.get("nombresDenunciante");
            String nombresDenunciante = (String) nombresDenuncianteMap.get("value");
            denunciaInfoRadicado.setNombresDenunciante(nombresDenunciante);

            Map<String, Object> apellidosDenuncianteMap = (Map<String, Object>) variablesMap.get("apellidosDenunciante");
            String apellidosDenunciante = (String) apellidosDenuncianteMap.get("value");
            denunciaInfoRadicado.setApellidosDenunciante(apellidosDenunciante);

            Map<String, Object> correoDenuncianteMap = (Map<String, Object>) variablesMap.get("correoDenunciante");
            String correoDenunciante = (String) correoDenuncianteMap.get("value");
            denunciaInfoRadicado.setCorreoDenunciante(correoDenunciante);

            Map<String, Object> descripcionDenuncianteMap = (Map<String, Object>) variablesMap.get("descripcionDenuncia");
            String descripcionDenunciante = (String) descripcionDenuncianteMap.get("value");
            denunciaInfoRadicado.setDescripcionDenuncia(descripcionDenunciante);
            return denunciaInfoRadicado;
        }else {
            return null;
        }
    }
}