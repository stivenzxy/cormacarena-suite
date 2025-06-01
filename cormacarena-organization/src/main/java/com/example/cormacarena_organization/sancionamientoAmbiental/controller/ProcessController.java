package com.example.cormacarena_organization.sancionamientoAmbiental.controller;


import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaProcesoVerificacionDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaRadicaDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RegistrarHechosFlagrancia;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.VerificacionHechosDenuncia;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.InformeTecnicoServiceImpl;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.RegistrarHechosFlagranciaImpl;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.VerificacionHechosDenunciaImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denuncia;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@RequestMapping("denuncias")
@RestController
public class ProcessController {
    private final RadicarDenunciasService radicarDenunciasService;
    private final SancionamientoAmbientalService sancionamientoAmbientalService;
    private final VerificacionHechosDenunciaImpl verificacionHechosDenuncia;
    private final InformeTecnicoServiceImpl informeTecnicoService;
    private final RegistrarHechosFlagranciaImpl registrarHechosFlagranciaService;
    @PostMapping("/radicar")
    RedirectView radicarDenuncia(@ModelAttribute DenunciaRadicaDTO denunciaRadicaDTO,
                                 @RequestParam("processId") String processId){

    if (denunciaRadicaDTO != null && processId != null){
        String resultado=radicarDenunciasService.enviarFormulario(denunciaRadicaDTO,processId);
        if(resultado.equals("Fine")){
            SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
            Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
            denuncia.setEstado("Radicada");
            sancionamientoAmbiental.setDenuncia(denuncia);
            sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
        }
        }
    return new RedirectView("/listaDenunciaSinRadicar");
    }
    @PostMapping("/hechosVerificados")
    RedirectView hechosVerificados(@ModelAttribute DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO,
                                 @RequestParam("processId") String processId){

        if (denunciaProcesoVerificacionDTO != null && processId != null){
            String resultado=verificacionHechosDenuncia.enviarFormulario(denunciaProcesoVerificacionDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Visita tecnica ya realizada");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/verificarHechos");
    }
    @PostMapping("/hechosRegistrados")
    RedirectView hechosRegistrados(@ModelAttribute DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO,
                                   @RequestParam("processId") String processId){

        if (denunciaProcesoVerificacionDTO != null && processId != null){
            String resultado=registrarHechosFlagranciaService.enviarFormulario(denunciaProcesoVerificacionDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Hechos registrados");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/registrarHechosFlagrancia");
    }

    @PostMapping("/informesTecnicosRealizados")
    RedirectView informesTecnicosRealizados(@ModelAttribute InformeTecnicoDTO informeTecnicoDTO,
                                   @RequestParam("processId") String processId){

        if (informeTecnicoDTO != null && processId != null){
            String resultado=informeTecnicoService.enviarFormulario(informeTecnicoDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("No se presento infracción");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/elaborarInformesTecnicos");
    }

}
