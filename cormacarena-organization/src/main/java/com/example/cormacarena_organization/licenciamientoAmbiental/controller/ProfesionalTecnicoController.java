package com.example.cormacarena_organization.licenciamientoAmbiental.controller;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.VisitaTecnicaDTO;
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
@RequestMapping("/profesional-tecnico")
public class ProfesionalTecnicoController {

    private final ProfesionalRevSolService profesionalRevSolService;

    @GetMapping
    public String mostrarListadoSolicitudesLicencia(Model model) {
        List<SolicitudPreviewDTO> solicitudes = profesionalRevSolService
                .obtenerSolicitudesVistaPrevia()
                .stream()
                .filter(s -> "Revision Tecnica".equals(s.getEstado()) ||
                        "Visita Realizada".equals(s.getEstado()))
                .collect(Collectors.toList());

        model.addAttribute("solicitudes", solicitudes);
        return "licenciamiento/profesional-tecnico/listadoSolRevTecnica";
    }

    @PostMapping("/registrar-visita")
    @ResponseBody
    public ResponseEntity<String> procesarVisita(@RequestParam("codigoSolicitud") String processId,
                                                 @RequestParam("visita") boolean visitaRealizada,
                                                 @RequestParam String fechaVisitaTecnica,
                                                 @RequestParam String profesionalAsignado,
                                                 @RequestParam boolean coherenciaImpactoEIA,
                                                 @RequestParam String observacionesVisitaTecnica) {
        try {
            VisitaTecnicaDTO visitaTecnicaDTO = new VisitaTecnicaDTO();
            visitaTecnicaDTO.setVisitaRealizada(visitaRealizada);
            visitaTecnicaDTO.setFechaVisitaTecnica(fechaVisitaTecnica);
            visitaTecnicaDTO.setProfesionalAsignado(profesionalAsignado);
            visitaTecnicaDTO.setCoherenciaImpactoEIA(coherenciaImpactoEIA);
            visitaTecnicaDTO.setObservacionesAdicionalesVisita(observacionesVisitaTecnica);

            profesionalRevSolService.realizarVisitaTecnica(processId, visitaTecnicaDTO, "Visita Realizada");

            return ResponseEntity.ok("Visita registrada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la visita: " + e.getMessage());
        }
    }

    @PostMapping("/elaborar-concepto")
    @ResponseBody
    public ResponseEntity<String> elaborarConceptoTecnico(@RequestParam("codigoSolicitud") String processId,
                                                          @RequestParam("fechaConceptoTecnico") String fechaConceptoTecnico) {
        try {
            profesionalRevSolService.emitirConceptoTecnico(processId, fechaConceptoTecnico,"Pagar Licencia");

            return ResponseEntity.ok("Concepto técnico emitido satisfactoriamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la visita: " + e.getMessage());
        }
    }
}