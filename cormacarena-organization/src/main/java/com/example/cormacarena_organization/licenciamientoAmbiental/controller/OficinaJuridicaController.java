package com.example.cormacarena_organization.licenciamientoAmbiental.controller;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.CoordinadorRevSolService;
import com.example.cormacarena_organization.licenciamientoAmbiental.service.OficinaJuridicaRevSolService;
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
@RequestMapping("asesoria-juridica")
public class OficinaJuridicaController {

    private final OficinaJuridicaRevSolService oficinaJuridicaRevSolService;
    private final ProfesionalRevSolService profesionalRevSolService;
    private final CoordinadorRevSolService coordinadorRevSolService;

    @GetMapping
    public String mostrarListadoSolicitudesLicencia(Model model) {
        List<SolicitudPreviewDTO> solicitudes = profesionalRevSolService
                .obtenerSolicitudesVistaPrevia()
                .stream()
                .filter(s -> "Concepto Tecnico Aprobado".equals(s.getEstado()))
                .collect(Collectors.toList());

        System.out.println("Solicitud en controlador: " + solicitudes);
        model.addAttribute("solicitudes", solicitudes);
        return "licenciamiento/asesoria-juridica/listadoSolicitudesAsJuridica";
    }

    @GetMapping("/detalle-solicitud/{codigoSolicitud}")
    public ResponseEntity<SolicitudDTO> obtenerDetalle(@PathVariable String codigoSolicitud) {
        SolicitudDTO dto = coordinadorRevSolService.getProcessVariablesById(codigoSolicitud);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/emitir-resolucion-administrativa")
    @ResponseBody
    public ResponseEntity<String> emitirResolucion(@RequestParam("codigoSolicitud") String processId,
                                                   @RequestParam("descripcionJuridica") String descripcionJuridica,
                                                   @RequestParam("fechaResolucion") String fechaResolucionJuridica) {
        try {
            oficinaJuridicaRevSolService.emitirResolucionAdministrativa(processId,fechaResolucionJuridica, descripcionJuridica, "Firmar Resolucion Final");
            return ResponseEntity.ok("Tarea aprobada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar la tarea: " + e.getMessage());
        }
    }

}
