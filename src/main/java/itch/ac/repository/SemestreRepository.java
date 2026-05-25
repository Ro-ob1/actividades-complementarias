package itch.ac.repository;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Semestre;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
	Semestre findByActivo(Integer activo);

	boolean existsByNombreIgnoreCase(String nombre);
}