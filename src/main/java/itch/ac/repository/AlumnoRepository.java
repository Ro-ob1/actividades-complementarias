package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itch.ac.model.Alumno;
import itch.ac.model.Persona;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
	Alumno findByNumControl(String numControl);

	List<Alumno> findByActivo(Integer activo);

	boolean existsByNumControlIgnoreCase(String numControl);

	Alumno findByPersona(Persona persona);

	@Query("SELECT a FROM Alumno a WHERE a.activo = 1 AND " +
	       "(LOWER(a.persona.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
	       "LOWER(a.persona.apellido) LIKE LOWER(CONCAT('%', :nombre, '%')))")
	List<Alumno> findActivosByNombre(@Param("nombre") String nombre);
}
