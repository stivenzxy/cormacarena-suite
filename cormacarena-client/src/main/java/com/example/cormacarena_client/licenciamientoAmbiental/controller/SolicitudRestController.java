package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.enums.EstadoProceso;
import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import com.example.cormacarena_client.utils.SolicitudMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SolicitudRestController {

    private final LicenciaAmbientalService licenciaAmbientalService;
    private final SolicitudLicenciaService solicitudLicenciaService;

    @GetMapping("/solicitud-borrador/{idSolicitante}")
    public ResponseEntity<SolicitudDTO> obtenerSolicitudEnBorradorPorId(@PathVariable String idSolicitante) {
        SolicitudLicencia solicitud = solicitudLicenciaService
                .obtenerPorEstadoYSolicitante(idSolicitante, EstadoProceso.BORRADOR.toString());

        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        SolicitudDTO dto = SolicitudMapper.toDTO(solicitud);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/solicitudes/{idSolicitante}")
    @ResponseBody
    public List<SolicitudLicencia> obtenerSolicitudes(@PathVariable String idSolicitante) {
        return solicitudLicenciaService.obtenerPorIdSolicitante(idSolicitante);
    }


    @PostMapping("/crear-solicitud-licencia")
    public RedirectView guardarFormulario(@ModelAttribute SolicitudDTO solicitudDTO,
                                          @RequestParam("soporteEIAPdf") MultipartFile soporteEIAPdf,
                                          RedirectAttributes redirectAttributes) {

        try {
            SolicitudLicencia solicitudExistente = solicitudLicenciaService
                    .obtenerPorEstadoYSolicitante(solicitudDTO.getIdSolicitante(), EstadoProceso.BORRADOR.toString());

            SolicitudLicencia solicitudLicencia;
            boolean esActualizacion = false;

            if (solicitudExistente != null) {
                solicitudLicencia = solicitudExistente;
                esActualizacion = true;
            } else {
                solicitudLicencia = new SolicitudLicencia();
            }

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
            solicitudLicencia.setEstado(EstadoProceso.BORRADOR.toString());

            String nombreArchivo = String.format("%s-soporteEIA.pdf", solicitudDTO.getIdSolicitante());
            solicitudLicencia.setNombreSoporteEIA(nombreArchivo);
            solicitudDTO.setNombreSoporteEIA(nombreArchivo);
            solicitudDTO.setEstado(EstadoProceso.BORRADOR.toString());

            String idProceso;
            if (esActualizacion && solicitudLicencia.getCodigoSolicitud() != null) {
                idProceso = solicitudLicencia.getCodigoSolicitud();
            } else {
                idProceso = licenciaAmbientalService.iniciarInstanciaProceso(solicitudDTO);
                solicitudLicencia.setCodigoSolicitud(idProceso);
            }

            if (esActualizacion) {
                solicitudLicenciaService.actualizarSolicitudExistente(idProceso, solicitudLicencia);
                redirectAttributes.addFlashAttribute("success", "Solicitud actualizada correctamente.");
                return new RedirectView("/solicitud-licencia-form");
            } else {
                solicitudLicenciaService.crearNuevaSolicitud(solicitudLicencia);
            }

        } catch (Exception e) {
            log.error("Error al procesar la solicitud: ", e);
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error al procesar la solicitud.");
            return new RedirectView("/solicitud-licencia-form");
        }

        redirectAttributes.addFlashAttribute("success", "Solicitud guardada correctamente.");
        return new RedirectView("/solicitud-licencia-form");
    }

    @PostMapping("enviar-formulario-solicitud")
    public RedirectView enviarFormularioActualizado(@ModelAttribute SolicitudDTO solicitudDTO,
                                                    RedirectAttributes redirectAttributes) {

        SolicitudLicencia solicitudGuardada = solicitudLicenciaService.obtenerPorEstadoYSolicitante(solicitudDTO.getIdSolicitante(),
                EstadoProceso.BORRADOR.toString());
        SolicitudLicencia solicitudLicencia = new SolicitudLicencia();

        if (solicitudGuardada == null) {
            redirectAttributes.addFlashAttribute("error", "Debe guardar primero la información");
            return new RedirectView("/solicitud-licencia-form");
        }

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
        solicitudLicencia.setEstado(EstadoProceso.REVISAR.toString());

        String nombreArchivo = String.format("%s-soporteEIA.pdf", solicitudDTO.getIdSolicitante());
        solicitudLicencia.setNombreSoporteEIA(nombreArchivo);
        solicitudDTO.setNombreSoporteEIA(nombreArchivo);

        solicitudLicenciaService.actualizarSolicitudExistente(solicitudGuardada.getCodigoSolicitud(), solicitudLicencia);
        licenciaAmbientalService.actualizarVariablesProceso(solicitudLicencia.getCodigoSolicitud(), solicitudLicencia);


        licenciaAmbientalService.completeTask(solicitudLicencia.getCodigoSolicitud());

        redirectAttributes.addFlashAttribute("success", "Solicitud enviada exitosamente!, su información será evaluada.");
        return new RedirectView("/listado-solicitudes?idSolicitante=" + solicitudDTO.getIdSolicitante());
    }
}
