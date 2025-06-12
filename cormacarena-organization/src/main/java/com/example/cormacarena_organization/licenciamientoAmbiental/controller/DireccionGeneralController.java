package com.example.cormacarena_organization.licenciamientoAmbiental.controller;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.CoordinadorRevSolService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.DireccionGeneralRevSolService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.ProfesionalRevSolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("direccion-general")
public class DireccionGeneralController {

    private final ProfesionalRevSolService profesionalRevSolService;
    private final CoordinadorRevSolService coordinadorRevSolService;
    private final DireccionGeneralRevSolService direccionGeneralRevSolService;

    @GetMapping
    public String mostrarListadoSolicitudesLicencia(Model model) {
        List<SolicitudPreviewDTO> solicitudes = profesionalRevSolService
                .obtenerSolicitudesVistaPrevia()
                .stream()
                .filter(s -> "Firmar Resolucion Final".equals(s.getEstado()))
                .collect(Collectors.toList());

        System.out.println("Solicitud en controlador: " + solicitudes);
        model.addAttribute("solicitudes", solicitudes);
        return "licenciamiento/direccion-general/listadoSolicitudesDirGeneral";
    }

    @GetMapping("/detalle-solicitud/{codigoSolicitud}")
    public ResponseEntity<SolicitudDTO> obtenerDetalle(@PathVariable String codigoSolicitud) {
        SolicitudDTO dto = coordinadorRevSolService.getProcessVariablesById(codigoSolicitud);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/otorgar-licencia")
    @ResponseBody
    public ResponseEntity<String> otorgarLicenciaAmbiental(@RequestParam("codigoSolicitud") String processId) {
        try {
            direccionGeneralRevSolService.otorgarLicenciaAmbiental(processId, "Licencia Otorgada");
            return ResponseEntity.ok("Tarea aprobada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar la tarea: " + e.getMessage());
        }
    }

}
