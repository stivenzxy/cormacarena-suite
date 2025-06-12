package com.example.cormacarena_client.solicitudPQRS.controller;

import com.example.cormacarena_client.solicitudPQRS.dto.FormularioDTO;
import com.example.cormacarena_client.solicitudPQRS.dto.MensajeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FormularioController {

    @Value("${camunda.url}")
    private String camundaUrlProcess;


    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("formulario", new FormularioDTO());
        return "pqrs/formulario";
    }

    @PostMapping("/enviar")
    public String enviarFormulario(@ModelAttribute FormularioDTO formularioDTO,
                                   @RequestParam("archivo") MultipartFile archivo,
                                   Model model) {

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        formularioDTO.setFechaSolicitud(fechaHoy);

        boolean adjuntaDocumento = !archivo.isEmpty();
        String nombreArchivo = adjuntaDocumento ? archivo.getOriginalFilename() : null;

        Map<String, Object> variables = new HashMap<>();
        variables.put("tipoSolicitud", Map.of("value", formularioDTO.getTipoSolicitud()));
        variables.put("descripcion", Map.of("value", formularioDTO.getDescripcion()));
        variables.put("nombre", Map.of("value", formularioDTO.getNombre()));
        variables.put("email", Map.of("value", formularioDTO.getEmail()));
        variables.put("fechaSolicitud", Map.of("value", fechaHoy));
        variables.put("adjDocumentos", Map.of("value", adjuntaDocumento));
        variables.put("nombreArchivo", Map.of("value", nombreArchivo != null ? nombreArchivo : ""));

        String camundaUrl = camundaUrlProcess+"process-definition/key/PQRS/start";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(camundaUrl, Map.of("variables", variables), String.class);

        model.addAttribute("mensaje", "Formulario enviado correctamente.");
        return "pqrs/index";
    }
}