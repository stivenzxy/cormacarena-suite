package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.enums.SectorProyecto;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import com.example.cormacarena_client.utils.SolicitudMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudLicenciaService solicitudLicenciaService;

    @GetMapping("/solicitudLicenciaForm")
    public String mostrarFormulario(@RequestParam(name = "idSolicitante", required = false) String idSolicitante,
                                    Model model) {
        model.addAttribute("solicitudDTO", new SolicitudDTO());
        model.addAttribute("sectores", SectorProyecto.values());

        if (idSolicitante != null && !idSolicitante.isBlank()) {
            List<SolicitudLicencia> solicitudes = solicitudLicenciaService.obtenerPorIdSolicitante(idSolicitante);
            model.addAttribute("solicitudes", solicitudes);
            model.addAttribute("idSolicitante", idSolicitante);
        }

        return "solicitudLicenciaForm";
    }
}
