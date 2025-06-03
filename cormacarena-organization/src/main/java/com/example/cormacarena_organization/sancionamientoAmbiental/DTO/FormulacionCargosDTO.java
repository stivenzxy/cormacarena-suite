package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FormulacionCargosDTO {
    private LocalDate fechaFormulacionCargos;
    private String nombreInfractor ;
    private String DescripcionFormulacionCargos;
    private Long idDenuncia;
    private String DescripcionDenuncia;
    private Long numeroDocumentoDenuciante;
    private TaskInfo taskInfo;
}
