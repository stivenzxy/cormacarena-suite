package com.example.cormacarena_client.controllerGeneral;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @GetMapping("/")
    public String mostrarInicio() {
        return "index";
    }
}
