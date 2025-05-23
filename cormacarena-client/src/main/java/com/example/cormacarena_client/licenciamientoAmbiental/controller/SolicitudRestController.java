package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SolicitudRestController {

    private final LicenciaAmbientalService licenciaAmbientalService;
    private final SolicitudLicenciaService solicitudLicenciaService;

    @GetMapping("solicitudes/{idSolicitante}")
    public SolicitudDTO buscarPorId(@PathVariable String idSolicitante) {
        SolicitudLicencia solicitud = solicitudLicenciaService.obtenerPorIdSolicitante(idSolicitante);
        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitante no encontrado");
        }

        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setNombreSolicitante(solicitud.getNombreSolicitante());
        solicitudDTO.setTipoIdentificacion(solicitud.getTipoIdentificacion());
        solicitudDTO.setIdSolicitante(solicitud.getIdSolicitante());
        solicitudDTO.setTelefono(solicitud.getTelefono());
        solicitudDTO.setEmail(solicitud.getEmail());
        solicitudDTO.setDireccionResidencia(solicitud.getDireccionResidencia());
        solicitudDTO.setNombreProyecto(solicitud.getNombreProyecto());
        solicitudDTO.setSectorProyecto(solicitud.getSectorProyecto());
        solicitudDTO.setValorProyecto(solicitud.getValorProyecto());
        solicitudDTO.setDepartamentoProyecto(solicitud.getDepartamentoProyecto());
        solicitudDTO.setMunicipioProyecto(solicitud.getMunicipioProyecto());
        return solicitudDTO;
    }


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
