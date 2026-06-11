package com.isaac.church.repository;

import com.isaac.church.entity.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
    boolean existsByEmail(String email);
}