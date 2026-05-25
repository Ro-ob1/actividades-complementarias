package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Constancia;
import itch.ac.model.Inscripcion;

public interface ConstanciaRepository extends JpaRepository<Constancia, Integer> {
	Constancia findByInscripcion(Inscripcion inscripcion);

	Constancia findByInscripcion_Id(Integer inscripcionId);
}