package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.FormulacionCargosDTO;

public interface FormulacionCargosService {
    public FormulacionCargosDTO getProcessVariablesById(String processId);
    public String enviarFormulario(FormulacionCargosDTO formulacionCargosDTO, String processId);
}
