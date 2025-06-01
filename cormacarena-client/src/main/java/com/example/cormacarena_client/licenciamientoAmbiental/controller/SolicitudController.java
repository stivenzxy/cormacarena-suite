package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import org.example.modelo.SectorProyecto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SolicitudController {


    @GetMapping("/solicitudLicenciaForm")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitudDTO", new SolicitudDTO());
        model.addAttribute("sectores", SectorProyecto.values());

        return "solicitudLicenciaForm";
    }

    @GetMapping("/solicitud_exito")
    public String mostrarSolicitudEnviada() {
        return "solicitud_exito";
    }
}
