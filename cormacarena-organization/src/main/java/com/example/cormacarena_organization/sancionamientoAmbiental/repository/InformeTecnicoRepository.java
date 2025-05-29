package com.example.cormacarena_organization.sancionamientoAmbiental.repository;

import com.example.cormacarena_organization.sancionamientoAmbiental.entity.InformeTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformeTecnicoRepository extends JpaRepository<InformeTecnico,Long> {
}
