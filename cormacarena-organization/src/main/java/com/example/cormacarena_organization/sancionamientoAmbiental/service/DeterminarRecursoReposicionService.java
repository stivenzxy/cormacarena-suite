package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.RecursosReposicionDTO;

public interface DeterminarRecursoReposicionService {
    public RecursosReposicionDTO getProcessVariablesById(String processId);
    public String enviarFormulario(RecursosReposicionDTO recursosReposicionDTO, String processId);
}
