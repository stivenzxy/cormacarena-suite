package com.example.cormacarena_client.sancionamientoAmbiental.service.impl;

import com.example.cormacarena_client.sancionamientoAmbiental.repository.DenunciasRadicadasRepository;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenunciasRadicadasService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.DenunciasRadicadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DenunciaRadicadasServiceImpl extends BaseServiceImpl<DenunciasRadicadas,Long> implements DenunciasRadicadasService {

    public DenunciaRadicadasServiceImpl(JpaRepository<DenunciasRadicadas, Long> repository) {
        super(repository);
    }
}
