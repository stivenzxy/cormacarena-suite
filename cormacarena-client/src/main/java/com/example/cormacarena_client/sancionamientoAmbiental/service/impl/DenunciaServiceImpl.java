package com.example.cormacarena_client.sancionamientoAmbiental.service.impl;


import com.example.cormacarena_client.sancionamientoAmbiental.repository.DenunciaRepository;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenunciaService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.DenuncianteService;
import com.example.cormacarena_client.sancionamientoAmbiental.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.modelo.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class DenunciaServiceImpl extends BaseServiceImpl<Denuncia,Long> implements DenunciaService {

    public DenunciaServiceImpl(JpaRepository<Denuncia, Long> repository) {
        super(repository);
    }
}
