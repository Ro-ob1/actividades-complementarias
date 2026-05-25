package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Integer> {
	boolean existsByClaveIgnoreCase(String clave);

	boolean existsByNombreIgnoreCase(String nombre);

	Carrera findByClaveIgnoreCase(String clave);
}
