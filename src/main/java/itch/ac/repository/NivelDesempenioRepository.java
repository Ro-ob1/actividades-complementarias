package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.NivelDesempenio;

public interface NivelDesempenioRepository extends JpaRepository<NivelDesempenio, Integer> {
	boolean existsByNombreIgnoreCase(String nombre);
}