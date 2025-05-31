package com.example.cormacarena_organization.licenciamientoAmbiental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @GetMapping("/licenciamiento")
    public String index() {
        return "licenciamiento/index";
    }
}
