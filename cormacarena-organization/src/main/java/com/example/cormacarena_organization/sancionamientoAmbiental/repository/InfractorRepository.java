package com.example.cormacarena_organization.sancionamientoAmbiental.repository;

import com.example.cormacarena_organization.sancionamientoAmbiental.entity.Infractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfractorRepository  extends JpaRepository<Infractor, Long> {
}
