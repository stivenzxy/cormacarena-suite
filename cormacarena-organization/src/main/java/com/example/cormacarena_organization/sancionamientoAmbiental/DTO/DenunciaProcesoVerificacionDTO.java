package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
public class DenunciaProcesoVerificacionDTO {
    Long idDenuncia;
    String DescripcionDenuncia;
    Long numeroDocumentoDenuciante;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDate fechaVisitaTecnica;
    String descripcionVisitaTecnica;
    Boolean infraccion;
    String encargadoVisitaTecnica;
    TaskInfo taskInfo;
}
