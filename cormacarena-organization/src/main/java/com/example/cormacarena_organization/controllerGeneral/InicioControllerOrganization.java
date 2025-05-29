package com.example.cormacarena_organization.controllerGeneral;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioControllerOrganization {
    @GetMapping("/")
    public String mostrarInicio() {
        return "index";
    }
}
