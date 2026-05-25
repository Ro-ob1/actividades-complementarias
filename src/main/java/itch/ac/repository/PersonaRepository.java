package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {
	boolean existsByEmailIgnoreCase(String email);
}