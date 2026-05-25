package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Actividad;
import itch.ac.model.Semestre;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
	List<Actividad> findBySemestre(Semestre semestre);

	List<Actividad> findByActivoAndSemestre(Integer activo, Semestre semestre);

	List<Actividad> findByNombreContainingIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCase(String nombre);
}