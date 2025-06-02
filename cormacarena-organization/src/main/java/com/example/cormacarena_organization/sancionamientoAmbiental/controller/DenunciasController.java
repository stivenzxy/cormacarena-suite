package com.example.cormacarena_organization.sancionamientoAmbiental.controller;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.*;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
public class DenunciasController {
    private  final RadicarDenunciasServiceImpl radicarDenunciasService;
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

    List<DenunciaInfoRadicado> processVariablesListCA = new ArrayList<>();
    List<DenunciaProcesoVerificacionDTO> processVariableDenunciasVerificacion = new ArrayList<>();
    List<InformeTecnicoDTO> processVariableInformesTecnicos = new ArrayList<>();
    List<ConceptoTecnicoDTO> processVariableConceptoTecnico = new ArrayList<>();
    List<ActoAdministrativaDTO> processVariableActoAdministrativoIncio = new ArrayList<>();
    List<ActoAdministrativaDTO> processVariableActoAdministrativoFormulacionCargos = new ArrayList<>();

    List<ActoAdministrativaDTO> processVariableActoAdministrativoIncioEtapaDefensaInfractor = new ArrayList<>();

    List<ActoAdministrativaDTO> processVariableActoAdministrativoConclusionesDenuncia = new ArrayList<>();

    List<ActoAdministrativaDTO> processVariableActoAdministrativoResolucionDenuncia = new ArrayList<>();
    List<FormulacionCargosDTO> processVariableFormulacionCargos = new ArrayList<>();
    List<DeterminarInfraccionDTO> processVariableDeterminarInfraccion = new ArrayList<>();
    List<ConceptoTecnicoPruebasDTO> processVariableConceptoTecnicoPrueba = new ArrayList<>();
    List<RecursosReposicionDTO> processVariableRecursosReposiconDTO = new ArrayList<>();
    List<DisminuicionSancionDTO> processVariableDisminuicionDTO = new ArrayList<>();




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
        List<String> processIds = registrarHechosFlagranciaService.getAllProcessByActivityId("Activity_0batq4k");
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
        List<String> processIds = registrarConceptoTecnicoService.getAllProcessByActivityId("Activity_1232Cone");
        processVariableConceptoTecnico.clear();
        for (String processId : processIds) {
            ConceptoTecnicoDTO conceptoTecnicoDTO = registrarConceptoTecnicoService.getProcessVariablesById(processId);
            TaskInfo taskInfo = registrarConceptoTecnicoService.getTaskInfoByProcessId(processId);
            conceptoTecnicoDTO.setTaskInfo(taskInfo);
            processVariableConceptoTecnico.add(conceptoTecnicoDTO
            );
        }
        model.addAttribute("denuncias",processVariableConceptoTecnico);
        return "conceptoTecnicoForm";
    }

    @GetMapping("/elaborarActosAdministrativos")
    private String elaborarActoAdministrativo(Model model){


        List<String> processIdsInicio = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_10v6x0h");
        List<String> processIdsElaborarActoAdministrativo = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_1kmxdrx");
        System.out.println("Elaborar Actos administrativos: "+processIdsElaborarActoAdministrativo);
        processVariableActoAdministrativoIncio.clear();
        for (String processId : processIdsInicio) {
            String childProcessId = registrarActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsElaborarActoAdministrativo.contains(childProcessId)) {
                ActoAdministrativaDTO dto = registrarActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Inicio de la penalización");
                TaskInfo taskInfo = registrarActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoIncio.add(dto);
            }
        }

        List<String> processIdsFormulacion = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_1yhukh6");
        processVariableActoAdministrativoFormulacionCargos.clear();
        for (String processId : processIdsFormulacion) {
            String childProcessId = registrarActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsElaborarActoAdministrativo.contains(childProcessId)) {
                ActoAdministrativaDTO dto = registrarActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Formulación de cargos");
                TaskInfo taskInfo = registrarActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoFormulacionCargos.add(dto);
            }
        }

        List<String> processIdsEtapaDefensa = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_063uwyu");
        processVariableActoAdministrativoIncioEtapaDefensaInfractor.clear();
        for (String processId : processIdsEtapaDefensa) {
            String childProcessId = registrarActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsElaborarActoAdministrativo.contains(childProcessId)) {
                ActoAdministrativaDTO dto = registrarActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Etapa de la defensa del infractor");
                TaskInfo taskInfo = registrarActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoIncioEtapaDefensaInfractor.add(dto);
            }
        }

        List<String> processIdsConclusiones = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_02u0eh5");
        processVariableActoAdministrativoConclusionesDenuncia.clear();
        for (String processId : processIdsConclusiones) {
            String childProcessId = registrarActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsElaborarActoAdministrativo.contains(childProcessId)) {
                ActoAdministrativaDTO dto = registrarActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Etapa de dialogo de las conclusiones de la denuncia");
                TaskInfo taskInfo = registrarActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoConclusionesDenuncia.add(dto);
            }
        }

        List<String> processIdsResolucion = registrarActoAdministrativoService.getAllProcessByActivityId("Activity_1avglay");
        processVariableActoAdministrativoResolucionDenuncia.clear();
        for (String processId : processIdsResolucion) {
            String childProcessId = registrarActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsElaborarActoAdministrativo.contains(childProcessId)) {
                ActoAdministrativaDTO dto = registrarActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Resolución de la denuncia");
                TaskInfo taskInfo = registrarActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoResolucionDenuncia.add(dto);
            }
        }

        model.addAttribute("denunciasInicio", processVariableActoAdministrativoIncio);
        model.addAttribute("denunciasFormulacionCargos", processVariableActoAdministrativoFormulacionCargos);
        model.addAttribute("denunciasEtapaDefensaInfracto", processVariableActoAdministrativoIncioEtapaDefensaInfractor);
        model.addAttribute("denunciasConclusionesDenuncia", processVariableActoAdministrativoConclusionesDenuncia);
        model.addAttribute("denunciasResolucionDenuncia", processVariableActoAdministrativoResolucionDenuncia);

        return "ActoAdministrativoForm";
    }
    @GetMapping("/reSolucionesActosAdministrativos")
    private String reSolucionesActoAdministrativo(Model model){


        List<String> processIdsInicio = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_10v6x0h");
        List<String> processIdsResolucionesActoAdministrativos = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_0yrmz41");
        System.out.println("Elaborar Actos administrativos: "+processIdsResolucionesActoAdministrativos);
        processVariableActoAdministrativoIncio.clear();
        for (String processId : processIdsInicio) {
            String childProcessId = resolucionActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsResolucionesActoAdministrativos.contains(childProcessId)) {
                ActoAdministrativaDTO dto = resolucionActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Inicio de la penalización");
                TaskInfo taskInfo = resolucionActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoIncio.add(dto);
            }
        }

        List<String> processIdsFormulacion = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_1yhukh6");
        processVariableActoAdministrativoFormulacionCargos.clear();
        for (String processId : processIdsFormulacion) {
            String childProcessId = resolucionActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsResolucionesActoAdministrativos.contains(childProcessId)) {
                ActoAdministrativaDTO dto = resolucionActoAdministrativoService.getProcessVariablesById(processId);
                System.out.println("profesional: "+ dto.getProfesionalActoAdministrativo());
                dto.setTipoActoAdministrativo("Formulación de cargos");
                TaskInfo taskInfo = resolucionActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoFormulacionCargos.add(dto);
            }
        }

        List<String> processIdsEtapaDefensa = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_063uwyu");
        processVariableActoAdministrativoIncioEtapaDefensaInfractor.clear();
        for (String processId : processIdsEtapaDefensa) {
            String childProcessId = resolucionActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsResolucionesActoAdministrativos.contains(childProcessId)) {
                ActoAdministrativaDTO dto = resolucionActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Etapa de la defensa del infractor");
                TaskInfo taskInfo = resolucionActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoIncioEtapaDefensaInfractor.add(dto);
            }
        }

        List<String> processIdsConclusiones = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_02u0eh5");
        processVariableActoAdministrativoConclusionesDenuncia.clear();
        for (String processId : processIdsConclusiones) {
            String childProcessId = resolucionActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsResolucionesActoAdministrativos.contains(childProcessId)) {
                ActoAdministrativaDTO dto = resolucionActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Etapa de dialogo de las conclusiones de la denuncia");
                TaskInfo taskInfo = resolucionActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoConclusionesDenuncia.add(dto);
            }
        }

        List<String> processIdsResolucion = resolucionActoAdministrativoService.getAllProcessByActivityId("Activity_1avglay");
        processVariableActoAdministrativoResolucionDenuncia.clear();
        for (String processId : processIdsResolucion) {
            String childProcessId = resolucionActoAdministrativoService.getChildProcessInstanceId(processId);

            // Solo continuar si childProcessId está en la lista de procesos elaborar acto administrativo
            if (childProcessId != null && processIdsResolucionesActoAdministrativos.contains(childProcessId)) {
                ActoAdministrativaDTO dto = resolucionActoAdministrativoService.getProcessVariablesById(processId);
                dto.setTipoActoAdministrativo("Resolución de la denuncia");
                TaskInfo taskInfo = resolucionActoAdministrativoService.getTaskInfoByProcessId(childProcessId);
                taskInfo.setProcessIdSuper(processId);
                dto.setTaskInfo(taskInfo);
                processVariableActoAdministrativoResolucionDenuncia.add(dto);
            }
        }

        model.addAttribute("denunciasInicio", processVariableActoAdministrativoIncio);
        model.addAttribute("denunciasFormulacionCargos", processVariableActoAdministrativoFormulacionCargos);
        model.addAttribute("denunciasEtapaDefensaInfracto", processVariableActoAdministrativoIncioEtapaDefensaInfractor);
        model.addAttribute("denunciasConclusionesDenuncia", processVariableActoAdministrativoConclusionesDenuncia);
        model.addAttribute("denunciasResolucionDenuncia", processVariableActoAdministrativoResolucionDenuncia);

        return "resolucionsActoAdministrativo";
    }

    @GetMapping("/formularCargos")
    private String formularCargos(Model model){
        List<String> processIds = formulacionCargosService.getAllProcessByActivityId("Activity_1xnfbt3");
        processVariableFormulacionCargos.clear();
        for (String processId : processIds) {
            FormulacionCargosDTO formulacionCargosDTO = formulacionCargosService.getProcessVariablesById(processId);
            TaskInfo taskInfo = formulacionCargosService.getTaskInfoByProcessId(processId);
            formulacionCargosDTO.setTaskInfo(taskInfo);
            processVariableFormulacionCargos.add(formulacionCargosDTO);
        }
        model.addAttribute("denuncias",processVariableFormulacionCargos);
        return "formularCargosForm";
    }
    @GetMapping("/denunciasADeterminar")
    private String denunciasADeterminar(Model model){
        List<String> processIds = Stream.concat(
                determinarInfractorService.getAllProcessByActivityId("Activity_1hc4usc").stream(),
                determinarInfractorService.getAllProcessByActivityId("Activity_0aqdeas").stream()
        ).collect(Collectors.toList());

        processVariableDeterminarInfraccion.clear();
        for (String processId : processIds) {
            DeterminarInfraccionDTO determinarInfraccionDTO = determinarInfractorService.getProcessVariablesById(processId);
            TaskInfo taskInfo = determinarInfractorService.getTaskInfoByProcessId(processId);
            determinarInfraccionDTO.setTaskInfo(taskInfo);
            processVariableDeterminarInfraccion.add(determinarInfraccionDTO);
        }
        model.addAttribute("denuncias",processVariableDeterminarInfraccion);
        return "determinarInfraccionForm";
    }
    @GetMapping("/conceptosTecnicosAElaborar")
    private String conceptosTecnicosAElaborar(Model model){
        List<String> processIds = conceptoTecnicoPruebasService.getAllProcessByActivityId("Activity_0l5a265");
        processVariableConceptoTecnicoPrueba.clear();
        for (String processId : processIds) {
            ConceptoTecnicoPruebasDTO conceptoTecnicoPruebasDTO = conceptoTecnicoPruebasService.getProcessVariablesById(processId);
            TaskInfo taskInfo = conceptoTecnicoPruebasService.getTaskInfoByProcessId(processId);
            conceptoTecnicoPruebasDTO.setTaskInfo(taskInfo);
            processVariableConceptoTecnicoPrueba.add(conceptoTecnicoPruebasDTO);
        }
        model.addAttribute("denuncias",processVariableConceptoTecnicoPrueba);
        return "conceptoTecnicosPruebaForm";
    }
    @GetMapping("/recursosReposicionADeterminar")
    private String recursosReposicionADeterminar(Model model){
        List<String> processIds = determinarRecursosReposicionService.getAllProcessByActivityId("Activity_0xrka9p");
        processVariableRecursosReposiconDTO.clear();
        for (String processId : processIds) {
            RecursosReposicionDTO recursosReposicionDTO = determinarRecursosReposicionService.getProcessVariablesById(processId);
            TaskInfo taskInfo = determinarRecursosReposicionService.getTaskInfoByProcessId(processId);
            recursosReposicionDTO.setTaskInfo(taskInfo);
            processVariableRecursosReposiconDTO.add(recursosReposicionDTO);
        }
        model.addAttribute("denuncias",processVariableRecursosReposiconDTO);
        return "recursosReposicionForm";
    }

    @GetMapping("/listaDeDisminuicion")
    private String listaDeDisminuicion(Model model){
        List<String> processIds = determinarDisminuicionSancionService.getAllProcessByActivityId("Activity_1u8j2ux");
        processVariableDisminuicionDTO.clear();
        for (String processId : processIds) {
            DisminuicionSancionDTO disminuicionSancionDTO = determinarDisminuicionSancionService.getProcessVariablesById(processId);
            TaskInfo taskInfo = determinarDisminuicionSancionService.getTaskInfoByProcessId(processId);
            disminuicionSancionDTO.setTaskInfo(taskInfo);
            processVariableDisminuicionDTO.add(disminuicionSancionDTO);
        }
        model.addAttribute("denuncias",processVariableDisminuicionDTO);
        return "disminuicionForm";
    }

}
