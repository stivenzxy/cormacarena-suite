package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SolicitudController {

    private final LicenciaAmbientalService licenciaAmbientalService;
    private final SolicitudLicenciaService solicitudLicenciaService;

    @PostMapping("/solicitar")
    public RedirectView procesarFormulario(@ModelAttribute SolicitudDTO solicitudDTO,
                                           @RequestParam("eiaDocumento") MultipartFile archivoEIA) {

        try {
        byte[] contenidoArchivo = archivoEIA.getBytes();
        SolicitudLicencia solicitudLicencia = new SolicitudLicencia();

        solicitudLicencia.setNombreSolicitante(solicitudDTO.getNombreSolicitante());
        solicitudLicencia.setTipoIdentificacion(solicitudDTO.getTipoIdentificacion());
        solicitudLicencia.setIdSolicitante(solicitudDTO.getIdSolicitante());
        solicitudLicencia.setTelefono(solicitudDTO.getTelefono());
        solicitudLicencia.setEmail(solicitudDTO.getEmail());
        solicitudLicencia.setDireccionResidencia(solicitudDTO.getDireccionResidencia());
        solicitudLicencia.setNombreProyecto(solicitudDTO.getNombreProyecto());
        solicitudLicencia.setSectorProyecto(solicitudDTO.getSectorProyecto());
        solicitudLicencia.setValorProyecto(solicitudDTO.getValorProyecto());
        solicitudLicencia.setDepartamentoProyecto(solicitudDTO.getDepartamentoProyecto());
        solicitudLicencia.setMunicipioProyecto(solicitudDTO.getMunicipioProyecto());
        solicitudLicencia.setDocumentoEIA(contenidoArchivo);
        solicitudLicenciaService.crearNuevaSolicitud(solicitudLicencia);
        solicitudDTO.setCodigoSolicitud(solicitudLicencia.getCodigoSolicitud());

        String processId = licenciaAmbientalService.startProcessInstance(solicitudDTO);
        System.out.println("***** PROCESS ID: " + processId);
        } catch (IOException e) {
            log.error("Error al subir archivo EIA: ", e);
        }

        return new RedirectView("/solicitud_exito");
    }
}
