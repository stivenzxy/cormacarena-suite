package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ConceptoTecnicoDTO {
    LocalDate fechaCreacionInformeTecnico;
    String razonesInfraccion;
    String encargadoVisitaTecnica;
    String descripcionVisitaTecnica;
    TaskInfo taskInfo;
    Long idDenuncia;
    String DescripcionDenuncia;
    Long numeroDocumentoDenuciante;
}
