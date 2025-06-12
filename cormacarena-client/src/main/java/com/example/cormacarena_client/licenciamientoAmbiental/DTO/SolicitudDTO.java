package com.example.cormacarena_client.licenciamientoAmbiental.DTO;

import lombok.Data;
import org.example.modelo.SectorProyecto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class SolicitudDTO {

    // Id del proceso
    private String codigoSolicitud;
    // Datos del Solicitante
    private String nombreSolicitante;
    private String tipoIdentificacion;
    private String idSolicitante;
    private String telefono;
    private String email;
    private String direccionResidencia;

    // Datos del Proyecto
    private String nombreProyecto;
    private SectorProyecto sectorProyecto;
    private Long valorProyecto;
    private String departamentoProyecto;
    private String municipioProyecto;
    private LocalDate fechaSolicitud;

    private String nombreSoporteEIA;
    private MultipartFile soporteEIAPdf;

    private String estado;
}
