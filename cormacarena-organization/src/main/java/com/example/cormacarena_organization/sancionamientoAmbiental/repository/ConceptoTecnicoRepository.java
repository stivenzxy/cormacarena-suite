package com.example.cormacarena_organization.sancionamientoAmbiental.repository;

import org.example.modelo.ConceptoTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoTecnicoRepository extends JpaRepository<ConceptoTecnico,Long> {
}
