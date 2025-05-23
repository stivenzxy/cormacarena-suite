package com.example.cormacarena_organization.licenciamientoAmbiental;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String mostrarInicio() {
        return "index";
    }
}
