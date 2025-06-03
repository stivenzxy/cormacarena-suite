package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActoAdministrativaDTO {
    LocalDate fechaActoAdministrativo;
    String tipoActoAdministrativo;
    String profesionalActoAdministrativo;
    String descripcionActoAdministrativo;
    TaskInfo taskInfo;
    Long idDenuncia;
    String DescripcionDenuncia;
    Long numeroDocumentoDenuciante;
    String tipoResolucion;
    String DescripcionResolucion;
    LocalDate fechaResolucion;

}
