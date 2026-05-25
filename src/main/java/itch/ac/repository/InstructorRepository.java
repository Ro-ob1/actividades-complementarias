package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itch.ac.model.Instructor;
import itch.ac.model.Persona;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
	List<Instructor> findByActivo(Integer activo);

	Instructor findByPersona(Persona persona);

	@Query("SELECT i FROM Instructor i WHERE i.activo = 1 AND " +
	       "(LOWER(i.persona.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
	       "LOWER(i.persona.apellido) LIKE LOWER(CONCAT('%', :nombre, '%')))")
	List<Instructor> findActivosByNombre(@Param("nombre") String nombre);
}