package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;

@Data
public class ConceptoTecnicoPruebasDTO {
    private Long idDenuncia;
    private String DescripcionDenuncia;
    private Long numeroDocumentoDenuciante;
    private TaskInfo taskInfo;
    private String nombreInfractor;
    private Boolean culpable;
    private String descripcionConclusiones;
}
