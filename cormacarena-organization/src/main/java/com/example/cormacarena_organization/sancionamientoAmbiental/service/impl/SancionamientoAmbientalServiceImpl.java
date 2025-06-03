package com.example.cormacarena_organization.sancionamientoAmbiental.service.impl;


import com.example.cormacarena_organization.sancionamientoAmbiental.repository.SancionamientoAmbientalRepository;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.SancionamientoAmbientalService;
import com.example.cormacarena_organization.sancionamientoAmbiental.service.base.BaseServiceImpl;
import org.example.modelo.SancionamientoAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SancionamientoAmbientalServiceImpl extends BaseServiceImpl<SancionamientoAmbiental,Long> implements SancionamientoAmbientalService {

    private  final SancionamientoAmbientalRepository sancionamientoAmbientalRepository;


    public SancionamientoAmbientalServiceImpl(JpaRepository<SancionamientoAmbiental, Long> repository, SancionamientoAmbientalRepository sancionamientoAmbientalRepository) {
        super(repository);
        this.sancionamientoAmbientalRepository = sancionamientoAmbientalRepository;
    }

    @Override
    public SancionamientoAmbiental encontrarSancionamientoAmbientalPorProcessId(String processId) {
        return sancionamientoAmbientalRepository.findSancionamientoAmbientalByProcessId(processId);
    }

    @Override
    public void actualizarSancionAmbiental(Long id, SancionamientoAmbiental sancionamientoAmbiental) {
        if(sancionamientoAmbientalRepository.existsById(id)){
            sancionamientoAmbiental.setId(id);
            sancionamientoAmbientalRepository.save(sancionamientoAmbiental);
        }
    }
}
