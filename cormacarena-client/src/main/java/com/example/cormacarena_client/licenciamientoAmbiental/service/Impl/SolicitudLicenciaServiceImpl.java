package com.example.cormacarena_client.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.repository.SolicitudRepository;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SolicitudLicenciaServiceImpl implements SolicitudLicenciaService {

    private final SolicitudRepository solicitudRepository;

    @Override
    public List<SolicitudLicencia> obtenerTodasLasSolicitudes () {
        return solicitudRepository.findAll();
    }

    @Override
    public void crearNuevaSolicitud (SolicitudLicencia solicitudLicencia) {
        solicitudRepository.save(solicitudLicencia);
    }
}
