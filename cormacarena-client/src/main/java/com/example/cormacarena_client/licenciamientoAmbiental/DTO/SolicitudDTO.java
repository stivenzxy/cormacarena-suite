package com.example.cormacarena_client.licenciamientoAmbiental.DTO;

import com.example.cormacarena_client.licenciamientoAmbiental.enums.SectorProyecto;
import lombok.Data;

import java.util.List;

@Data
public class SolicitudDTO {

    private Long codigoSolicitud;
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
    private String valorProyecto;
    private String departamentoProyecto;
    private String municipioProyecto;
    private byte[] documentoEIA;
}
