package com.example.cormacarena_organization.solicitudPQRS.dto;

import lombok.Data;

@Data
public class VerificarCompetenciaDTO {
    private Boolean competencia;

    // Getters y setters
    public Boolean getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Boolean competencia) {
        this.competencia = competencia;
    }



}