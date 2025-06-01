package com.example.cormacarena_client.sancionamientoAmbiental.controller;


import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteRealizaDenunciaService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.DenunciaServiceImpl;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.DenuncianteServiceImpl;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.SancionamientoAmbientalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denuncia;
import org.example.modelo.Denunciante;
import org.example.modelo.SancionamientoAmbiental;
import org.example.modelo.State;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("denuncia")
public class DenunciaRestController {

    private final DenuncianteServiceImpl denuncianteServiceImpl;
    private final DenunciaServiceImpl denunciaService;
    private final DenuncianteRealizaDenunciaService denuncianteRealizaDenunciaService;
    private final SancionamientoAmbientalServiceImpl sancionamientoAmbientalService;
    @PostMapping("/crear")
    public RedirectView crearDenuncia(@ModelAttribute DenunciaDTO denunciaDTO,
                                      @RequestParam("soporteDenuncia")MultipartFile soporteDenuncia,
                                      RedirectAttributes redirectAttributes) throws IOException {
        Denunciante denunciante = new Denunciante();
        denunciante.setNombresDenunciante(denunciaDTO.getNombresDenunciante());
        denunciante.setApellidoDenunciante(denunciaDTO.getApellidosDenunciante());
        denunciante.setCorreoDenunciante(denunciaDTO.getCorreoDenunciante());
        denunciante.setTipoIdentificacionDenunciante(denunciaDTO.getTipoIdentificacionDenunciante());
        denunciante.setNumeroIdentificacion(denunciaDTO.getNumeroIdentificacionDenunciante());

        denuncianteServiceImpl.save(denunciante);

        String rutaOriginalSoporte = soporteDenuncia.getOriginalFilename();
        Denuncia denuncia = new Denuncia();
        denuncia.setDescripcionDenuncia(denunciaDTO.getDescripcionDenuncia());
        denuncia.setDenunciante(denunciante);
        denuncia.setSoporteDenuncia(rutaOriginalSoporte);
        String processId = denuncianteRealizaDenunciaService.startProcessInstance(denunciaDTO, rutaOriginalSoporte);
        denuncia.setEstado("En proceso de radicación");
        denunciaService.save(denuncia);

        SancionamientoAmbiental sancionamientoAmbiental = new SancionamientoAmbiental();
        sancionamientoAmbiental.setProcessId(processId);
        sancionamientoAmbiental.setStatus(State.DRAFT.toString());
        sancionamientoAmbiental.setDenuncia(denuncia);

        sancionamientoAmbientalService.save(sancionamientoAmbiental);

        //redirectAttributes.addAttribute("Identificación denunciante",denunciante.getNumeroIdentificacion());
        System.out.println("***** PROCESS IDD: "+processId);
        redirectAttributes.addFlashAttribute("denunciaDTO", denunciaDTO);
        redirectAttributes.addAttribute("processId", processId);

        return new RedirectView("/realizarDenuncia");
    }

}
