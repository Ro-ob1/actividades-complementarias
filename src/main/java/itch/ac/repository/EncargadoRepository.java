package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itch.ac.model.Encargado;
import itch.ac.model.Persona;

public interface EncargadoRepository extends JpaRepository<Encargado, Integer> {
	List<Encargado> findByActivo(Integer activo);

	Encargado findByPersona(Persona persona);

}