package com.example.cormacarena_organization.sancionamientoAmbiental.repository;

import org.example.modelo.ActoAdministrativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActoAdministrativoRepository extends JpaRepository<ActoAdministrativo, Long> {
}
