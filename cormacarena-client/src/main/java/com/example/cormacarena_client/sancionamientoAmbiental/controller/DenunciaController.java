package com.example.cormacarena_client.sancionamientoAmbiental.controller;

import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaInfoDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.DenunciaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denuncia;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class DenunciaController {

    private final DenunciaServiceImpl denunciaService;
    @GetMapping("/realizarDenuncia")
    public String denunciaForm(@RequestParam(value = "processId", required = false)String processId, Model model) {
        model.addAttribute("processId", processId);
        System.out.println("ProcessId recibido en GET: " + processId);  // LOG para debug
        if (!model.containsAttribute("denunciaDTO")) {
            model.addAttribute("denunciaDTO", new DenunciaDTO());
        }
        return "denunciaForm";
    }
    @GetMapping("/consultar")
    public String denunciaForm(@RequestParam(value = "idDenuncia", required = false) Long idDenuncia,
                               Model model){

        model.addAttribute("idDenuncia", idDenuncia);

        if(idDenuncia != null){
            try {
                Denuncia denuncia = denunciaService.findById(idDenuncia);
                if(denuncia != null){
                    DenunciaInfoDTO denunciaInfoDTO = new DenunciaInfoDTO();
                    denunciaInfoDTO.setId(denuncia.getIdDenuncia());
                    Long numeroRadicado = null;
                    if (denuncia.getDenunciaRadicada() != null) {
                        numeroRadicado = denuncia.getDenunciaRadicada().getNumeroRadicado();
                    }
                    denunciaInfoDTO.setNumeroradicado(numeroRadicado);
                    denunciaInfoDTO.setEstado(denuncia.getEstado());
                    System.out.println(denuncia.getIdDenuncia());
                    model.addAttribute("denuncia",denunciaInfoDTO);

                }

            }catch (Exception e) {
                // Log opcional
                System.out.println("Error al buscar denuncia: " + e.getMessage());

                // Mensaje opcional al usuario
                model.addAttribute("error", "No se pudo consultar la denuncia. Intenta nuevamente.");
            }

        }

        return "consultarDenuncia";
    }
}
