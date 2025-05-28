package com.example.cormacarena_organization.sancionamientoAmbiental.controller;

import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaInfoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaInfoRadicado;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.RadicarDenunciasService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.impl.RadicarDenunciasServiceImpl;
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
    List<DenunciaInfoRadicado> processVariablesListCA = new ArrayList<>();

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
}
