package com.example.cormacarena_organization.licenciamientoAmbiental.DTO;

import lombok.Data;

@Data
public class VisitaTecnicaDTO {
    private boolean visitaRealizada;
    private String fechaVisitaTecnica;
    private String profesionalAsignado;
    private boolean coherenciaImpactoEIA;
    private String observacionesAdicionalesVisita;
}
