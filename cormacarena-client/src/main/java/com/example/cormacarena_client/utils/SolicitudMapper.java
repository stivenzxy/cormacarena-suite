package com.example.cormacarena_client.utils;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;

public class SolicitudMapper {

    public static SolicitudDTO toDTO(SolicitudLicencia entity) {
        SolicitudDTO solicitudDto = new SolicitudDTO();
        solicitudDto.setTipoIdentificacion(entity.getTipoIdentificacion());
        solicitudDto.setIdSolicitante(entity.getIdSolicitante());
        solicitudDto.setNombreSolicitante(entity.getNombreSolicitante());
        solicitudDto.setDireccionResidencia(entity.getDireccionResidencia());
        solicitudDto.setTelefono(entity.getTelefono());
        solicitudDto.setEmail(entity.getEmail());

        solicitudDto.setNombreProyecto(entity.getNombreProyecto());
        solicitudDto.setSectorProyecto(entity.getSectorProyecto());
        solicitudDto.setValorProyecto(entity.getValorProyecto());
        solicitudDto.setDepartamentoProyecto(entity.getDepartamentoProyecto());
        solicitudDto.setMunicipioProyecto(entity.getMunicipioProyecto());

        solicitudDto.setNombreSoporteEIA(entity.getNombreSoporteEIA());
        solicitudDto.setEstado(entity.getEstado());

        return solicitudDto;
    }
}
