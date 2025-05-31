package com.example.cormacarena_organization.licenciamientoAmbiental.controller;


import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.CoordinadorRevSolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coordinador-grupo")
public class CoordinadorGrupoController {

    private final CoordinadorRevSolService coordinadorRevSolService;

    @GetMapping
    public String mostrarListadoSolicitudesLicencia(Model model) {
        List<SolicitudPreviewDTO> solicitudes = coordinadorRevSolService.obtenerSolicitudesVistaPrevia();
        model.addAttribute("solicitudes", solicitudes);

        return "licenciamiento/coordinador-grupo/listadoSolicitudesLicencia";
    }

    @GetMapping("/evaluacion-solicitud/{codigoSolicitud}")
    public ResponseEntity<SolicitudDTO> obtenerDetalle(@PathVariable String codigoSolicitud) {
        SolicitudDTO dto = coordinadorRevSolService.getProcessVariablesById(codigoSolicitud);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/aprobacion-formulario")
    @ResponseBody
    public ResponseEntity<String> aprobarTarea(@RequestParam("processId") String processId) {
        try {
            coordinadorRevSolService.aprobarSolicitud(processId);
            return ResponseEntity.ok("Tarea aprobada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar la tarea: " + e.getMessage());
        }
    }

    @PostMapping("/rechazo-formulario")
    @ResponseBody
    public ResponseEntity<String> rechazarTarea(@RequestParam("processId") String processId) {
        try {
            coordinadorRevSolService.rechazarSolicitud(processId);
            return ResponseEntity.ok("Tarea rechazada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al rechazar la tarea: " + e.getMessage());
        }
    }
}
