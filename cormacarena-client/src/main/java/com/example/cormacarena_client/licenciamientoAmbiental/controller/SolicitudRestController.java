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
public class SolicitudRestController {

    private final LicenciaAmbientalService licenciaAmbientalService;
    private final SolicitudLicenciaService solicitudLicenciaService;

    @PostMapping("/solicitar")
    public RedirectView procesarFormulario(@ModelAttribute SolicitudDTO solicitudDTO,
                                           @RequestParam("soporteEIAPdf") MultipartFile soporteEIAPdf) {

        try {
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

        String nombreOriginal = soporteEIAPdf.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }

        String nombreArchivo = String.format("%s-soporteEIA%s", solicitudDTO.getIdSolicitante(), extension);

        solicitudLicencia.setNombreSoporteEIA(nombreArchivo);
        solicitudLicenciaService.crearNuevaSolicitud(solicitudLicencia);
        solicitudDTO.setCodigoSolicitud(solicitudLicencia.getCodigoSolicitud());
        solicitudDTO.setNombreSoporteEIA(nombreArchivo);

        String processId = licenciaAmbientalService.startProcessInstance(solicitudDTO);
        System.out.println("***** PROCESS ID: " + processId);
        } catch (Exception e) {
            log.error("Error al obtener la instancia del proceso: ", e);
        }

        return new RedirectView("/solicitud_exito");
    }
}
