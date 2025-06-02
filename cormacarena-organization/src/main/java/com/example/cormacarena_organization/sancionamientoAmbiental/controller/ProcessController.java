package com.example.cormacarena_organization.sancionamientoAmbiental.controller;


import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.*;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denuncia;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.web.bind.annotation.*;
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
    private final RegistrarConceptoTecnicoImpl registrarConceptoTecnicoService;
    private final RegistrarActoAdministrativoServiceImpl registrarActoAdministrativoService;
    private final ResolucionActoAdministrativoImpl resolucionActoAdministrativoService;
    private final FormulacionCargosServiceImpl formulacionCargosService;
    private final DeterminarInfraccionServiceImpl determinarInfractorService;
    private final ConceptoTecnicoPruebasServiceImpl conceptoTecnicoPruebasService;
    private final DeterminarRecursosReposicionServiceImpl determinarRecursosReposicionService;
    private final DeterminarDisminuicionSancionServiceImpl determinarDisminuicionSancionService;

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
    @PostMapping("/conceptosTecnicosRegistrados")
    RedirectView conceptoTecnicosRegistrados(@ModelAttribute ConceptoTecnicoDTO conceptoTecnicoDTO,
                                   @RequestParam("processId") String processId) {

        if (conceptoTecnicoDTO != null && processId != null) {
            String resultado = registrarConceptoTecnicoService.enviarFormulario(conceptoTecnicoDTO, processId);
            if (resultado.equals("Fine")) {
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Concepto tecnico registrado en proceso de judilización");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(), sancionamientoAmbiental);
            } else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/registrarConceptoTecnico");
    }
    @PostMapping("/elaborarActoAdministrativo")
    RedirectView elaborarActoAdministrativo(@ModelAttribute ActoAdministrativaDTO actoAdministrativaDTO,
                                             @RequestParam("processId") String processId) {

        if (actoAdministrativaDTO != null && processId != null) {
            System.out.println("Fecha: "+ actoAdministrativaDTO.getFechaActoAdministrativo() + actoAdministrativaDTO.getDescripcionActoAdministrativo());

            String resultado = registrarActoAdministrativoService.enviarFormulario(actoAdministrativaDTO, processId);
            if (resultado.equals("Fine")) {
                String superProcessId = registrarActoAdministrativoService.getParentProcessInstanceId(processId);
                System.out.println("padre: "+superProcessId);
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(superProcessId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Acto administrativo");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(), sancionamientoAmbiental);
            } else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/elaborarActosAdministrativos");
    }
    @PostMapping("/resolverActoAdministrativo")
    RedirectView resolverActoAdministratuvi(@ModelAttribute ActoAdministrativaDTO actoAdministrativaDTO,
                                            @RequestParam("processId") String processId) {

        if (actoAdministrativaDTO != null && processId != null) {
            System.out.println("Fecha: "+ actoAdministrativaDTO.getFechaActoAdministrativo() + actoAdministrativaDTO.getDescripcionActoAdministrativo());
            String resultado = resolucionActoAdministrativoService.enviarFormulario(actoAdministrativaDTO, processId);
            if (resultado.equals("Fine")) {
                String superProcessId = resolucionActoAdministrativoService.getParentProcessInstanceId(processId);
                System.out.println("padre: "+superProcessId);
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(superProcessId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Acto administrativo");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(), sancionamientoAmbiental);
            } else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/reSolucionesActosAdministrativos");
    }
    @PostMapping("/enviaCargosFormulados")
    RedirectView enviaCargosFormulados(@ModelAttribute FormulacionCargosDTO formulacionCargosDTO,
                                       @RequestParam("processId") String processId){

        if (formulacionCargosDTO != null && processId != null){
            String resultado=formulacionCargosService.enviarFormulario(formulacionCargosDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Cargos Formulados");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/formularCargos");
    }
    @PostMapping("/determinarInfraccion")
    RedirectView determinarInfraccion(@ModelAttribute DeterminarInfraccionDTO determinarInfraccionDTO,
                                       @RequestParam("processId") String processId){

        if (determinarInfraccionDTO != null && processId != null){
            String resultado=determinarInfractorService.enviarFormulario(determinarInfraccionDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Infraccion Determinada");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/denunciasADeterminar");
    }
    @PostMapping("/enviarConceptoTecnicoPruebas")
    RedirectView enviarConceptoTecnicoPruebas(@ModelAttribute ConceptoTecnicoPruebasDTO conceptoTecnicoPruebasDTO,
                                      @RequestParam("processId") String processId){

        if (conceptoTecnicoPruebasDTO != null && processId != null){
            String resultado=conceptoTecnicoPruebasService.enviarFormulario(conceptoTecnicoPruebasDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Concepto Tecnico de las pruebas de la denuncia Emitidos");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/conceptosTecnicosAElaborar");
    }
    @PostMapping("/determinarRecursosReposicion")
    RedirectView determinarRecursosReposicion(@ModelAttribute RecursosReposicionDTO recursosReposicionDTO,
                                              @RequestParam("processId") String processId){

        if (recursosReposicionDTO != null && processId != null){
            String resultado=determinarRecursosReposicionService.enviarFormulario(recursosReposicionDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Recursos de reposicion determinados");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/recursosReposicionADeterminar");
    }
    @PostMapping("/determinarDisminuicionSancion")
    RedirectView determinarDisminuicionSancion(@ModelAttribute DisminuicionSancionDTO disminuicionSancionDTO,
                                              @RequestParam("processId") String processId){

        if (disminuicionSancionDTO != null && processId != null){
            String resultado=determinarDisminuicionSancionService.enviarFormulario(disminuicionSancionDTO,processId);
            if(resultado.equals("Fine")){
                SancionamientoAmbiental sancionamientoAmbiental = sancionamientoAmbientalService.encontrarSancionamientoAmbientalPorProcessId(processId);
                Denuncia denuncia = sancionamientoAmbiental.getDenuncia();
                denuncia.setEstado("Sancion disminuidad");
                sancionamientoAmbiental.setDenuncia(denuncia);
                sancionamientoAmbientalService.actualizarSancionAmbiental(sancionamientoAmbiental.getId(),sancionamientoAmbiental);
            }
            else {
                System.out.println("Error no se puede actualizar los datos");
            }
        }
        return new RedirectView("/listaDeDisminuicion");
    }
}
