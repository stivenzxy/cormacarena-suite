package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import org.example.modelo.SectorProyecto;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SolicitudLicencia;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudLicenciaService solicitudLicenciaService;

    @GetMapping("/licenciamiento")
    public String mostrarIndex() {
        return "licenciamiento/index";
    }

    @GetMapping("/solicitud-licencia-form")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitudDTO", new SolicitudDTO());
        model.addAttribute("sectores", SectorProyecto.values());

        return "licenciamiento/solicitudLicenciaForm";
    }

    @GetMapping("/listado-solicitudes")
    public String mostrarListadoSolicitudes(@RequestParam(name = "idSolicitante", required = false) String idSolicitante,
                                           Model model) {
        if (idSolicitante != null && !idSolicitante.isBlank()) {
            List<SolicitudLicencia> solicitudes = solicitudLicenciaService.obtenerPorIdSolicitante(idSolicitante);
            model.addAttribute("solicitudes", solicitudes);
            model.addAttribute("idSolicitante", idSolicitante);
        }

        return "licenciamiento/listadoSolicitudes";
    }
}
