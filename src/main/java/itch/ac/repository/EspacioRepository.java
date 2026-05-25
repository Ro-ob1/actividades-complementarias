package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Espacio;

public interface EspacioRepository extends JpaRepository<Espacio, Integer> {
	boolean existsByNombreIgnoreCase(String nombre);
}