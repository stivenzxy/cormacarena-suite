package com.example.cormacarena_organization.sancionamientoAmbiental.repository;


import org.example.modelo.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {
}
