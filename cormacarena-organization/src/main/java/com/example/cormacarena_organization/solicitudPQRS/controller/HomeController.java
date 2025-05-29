package com.example.cormacarena_organization.solicitudPQRS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/pqrs")
    public String index() {
        return "pqrs/index";
    }
}
