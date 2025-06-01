package com.example.cormacarena_organization.sancionamientoAmbiental.controller;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.*;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.VerificacionHechosDenuncia;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class DenunciasController {
    private  final RadicarDenunciasServiceImpl radicarDenunciasService;
    private final VerificacionHechosDenunciaImpl verificacionHechosDenuncia;
    private final InformeTecnicoServiceImpl informeTecnicoService;
    private final RegistrarHechosFlagranciaImpl registrarHechosFlagranciaService;
    private final RegistrarConceptoTecnicoImpl registrarConceptoTecnicoService;
    List<DenunciaInfoRadicado> processVariablesListCA = new ArrayList<>();
    List<DenunciaProcesoVerificacionDTO> processVariableDenunciasVerificacion = new ArrayList<>();
    List<InformeTecnicoDTO> processVariableInformesTecnicos = new ArrayList<>();
    List<ConceptoTecnicoDTO> processVariableConceptoTecnico = new ArrayList<>();
    @GetMapping("/denunciasInicio")
    private String denunciaIncio(){
        return "inicioSeguimientoDenuncias";
    }
    @GetMapping("/listaDenunciaSinRadicar")
    private String listaDenunciaSinRadicar(Model model){
        List<String> processIds = radicarDenunciasService.getAllProcessByActivityId("Activity_0v8jnti");
        processVariablesListCA.clear();
        for (String processId : processIds) {
            DenunciaInfoRadicado denunciaInfoRadicado = radicarDenunciasService.getProcessVariablesById(processId);
            TaskInfo taskInfo = radicarDenunciasService.getTaskInfoByProcessId(processId);
            denunciaInfoRadicado.setTaskInfo(taskInfo);
            processVariablesListCA.add(denunciaInfoRadicado);
        }
        model.addAttribute("denuncias",processVariablesListCA);
        return "radicarDenuncia";
    }
    @GetMapping("/verificarHechos")
    private String verificarHechos(Model model){
        List<String> processIds = verificacionHechosDenuncia.getAllProcessByActivityId("Activity_0sw7kwk");
        processVariableDenunciasVerificacion.clear();
        for (String processId : processIds) {
            DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO = verificacionHechosDenuncia.getProcessVariablesById(processId);
            TaskInfo taskInfo = verificacionHechosDenuncia.getTaskInfoByProcessId(processId);
            denunciaProcesoVerificacionDTO.setTaskInfo(taskInfo);
            processVariableDenunciasVerificacion.add(denunciaProcesoVerificacionDTO);
        }
        model.addAttribute("denuncias",processVariableDenunciasVerificacion);
        return "verificacionHechos";
    }
    @GetMapping("/registrarHechosFlagrancia")
    private String registrarHechosFlagrancia(Model model){
        List<String> processIds = registrarHechosFlagranciaService.getAllProcessByActivityId("Activity_04zk5n3");
        processVariableDenunciasVerificacion.clear();
        for (String processId : processIds) {
            DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO = verificacionHechosDenuncia.getProcessVariablesById(processId);
            TaskInfo taskInfo = registrarHechosFlagranciaService.getTaskInfoByProcessId(processId);
            denunciaProcesoVerificacionDTO.setTaskInfo(taskInfo);
            processVariableDenunciasVerificacion.add(denunciaProcesoVerificacionDTO);
        }
        model.addAttribute("denuncias",processVariableDenunciasVerificacion);
        return "registrarHechosFlagranciaForm";
    }
    @GetMapping("/elaborarInformesTecnicos")
    private String elaborarInformesTecnicos(Model model){
        List<String> processIds = informeTecnicoService.getAllProcessByActivityId("Activity_0dtdoio");
        processVariableInformesTecnicos.clear();
        for (String processId : processIds) {
            InformeTecnicoDTO informeTecnicoDTO = informeTecnicoService.getProcessVariablesById(processId);
            TaskInfo taskInfo = informeTecnicoService.getTaskInfoByProcessId(processId);
            informeTecnicoDTO.setTaskInfo(taskInfo);
            processVariableInformesTecnicos.add(informeTecnicoDTO);
        }
        model.addAttribute("denuncias",processVariableInformesTecnicos);
        return "informesTecnicosForm";
    }
    @GetMapping("/registrarConceptoTecnico")
    private String registrarConceptoTecnico(Model model){
        List<String> processIds = registrarConceptoTecnicoService.getAllProcessByActivityId("Actividad_conceptoTecnico");
        processVariableConceptoTecnico.clear();
        for (String processId : processIds) {
            ConceptoTecnicoDTO conceptoTecnicoDTO = registrarConceptoTecnicoService.getProcessVariablesById(processId);
            TaskInfo taskInfo = registrarConceptoTecnicoService.getTaskInfoByProcessId(processId);
            conceptoTecnicoDTO.setTaskInfo(taskInfo);
            processVariableConceptoTecnico.add(conceptoTecnicoDTO
            );
        }
        model.addAttribute("denuncias",processVariableInformesTecnicos);
        return "conceptoTecnicoForm";
    }
}
