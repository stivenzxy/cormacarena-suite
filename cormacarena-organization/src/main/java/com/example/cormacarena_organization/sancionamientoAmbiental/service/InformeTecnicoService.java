package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaProcesoVerificacionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseServiceOrganization;
import org.example.modelo.InformeTecnico;
import org.springframework.stereotype.Service;

@Service
public interface InformeTecnicoService {
    public InformeTecnicoDTO getProcessVariablesById(String processId);
    public String enviarFormulario(InformeTecnicoDTO informeTecnicoDTO, String processId);
}
