package com.example.cormacarena_client.sancionamientoAmbiental.controller;

import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.entity.Denuncia;
import com.example.cormacarena_client.sancionamientoAmbiental.entity.SancionamientoAmbiental;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenunciaService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteRealizaDenunciaService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.DenunciaServiceImpl;
import com.example.cormacarena_client.sancionamientoAmbiental.service.impl.SancionamientoAmbientalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ProcessController {

    private final DenuncianteRealizaDenunciaService denunciaService;
    private final SancionamientoAmbientalServiceImpl sancionamientoAmbientalService;

    @PostMapping("/complete")
    public String completeTask(@ModelAttribute DenunciaDTO denunciaDTO,
                                @RequestParam String processId){

        if(processId !=null && denunciaDTO != null){
            System.out.println("Process: "+ processId);
            SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
            denunciaService.completeTask(processId, denunciaDTO);
            return "redirect:/consultar?idDenuncia="+sancionamientoAmbiental.getDenuncia().getIdDenuncia();
        }else{
        return "/realizarDenuncia";
        }
    }

}
