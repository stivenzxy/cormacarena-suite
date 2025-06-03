package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;

@Data
public class RecursosReposicionDTO {
    private TaskInfo taskInfo;
    private String nombreInfractor;
    private Boolean culpable;
    private Long idDenuncia;
    private String DescripcionDenuncia;
    private Long numeroDocumentoDenuciante;
    private String tipoRecursoReposicion;
}
