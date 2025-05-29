package com.example.cormacarena_client.sancionamientoAmbiental.service;

import com.example.cormacarena_client.sancionamientoAmbiental.entity.SancionamientoAmbiental;
import com.example.cormacarena_client.sancionamientoAmbiental.repository.SancionamientoAmbientalRepository;

import java.util.List;

public interface SancionamientoAmbientalService {

    SancionamientoAmbiental encontrarSancionamientoAmbientalPorProcessId(String processId);

    void actualizarSancionAmbiental(Long id, SancionamientoAmbiental sancionamientoAmbiental);

}
