package com.example.cormacarena_client.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_client.sancionamientoAmbiental.repository.DenuncianteRepository;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denunciante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DenuncianteServiceImpl extends BaseServiceImpl<Denunciante,Long> implements DenuncianteService {

    public DenuncianteServiceImpl(JpaRepository<Denunciante, Long> repository) {
        super(repository);
    }
}
