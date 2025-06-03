package com.example.cormacarena_client.solicitudPQRS.controller;

import com.example.cormacarena_client.solicitudPQRS.dto.MensajeDTO;

import org.example.modelo.Bandeja;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/pqrs")
    public String index() {
        return "pqrs/index";
    }

    @GetMapping("/bandeja")
    public String verBandeja(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:9090/api/bandeja";

        // Usa Bandeja[] porque estás trayendo entidades directamente
        Bandeja[] bandejaArray = restTemplate.getForObject(apiUrl, Bandeja[].class);

        List<MensajeDTO> mensajes = new ArrayList<>();
        for (Bandeja bandeja : bandejaArray) {
            MensajeDTO dto = new MensajeDTO();
            dto.setMensaje(bandeja.getMensaje());
            mensajes.add(dto);
        }

        model.addAttribute("mensajes", mensajes);
        return "pqrs/bandeja";
    }
}
