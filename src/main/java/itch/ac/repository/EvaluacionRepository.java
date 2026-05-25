package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Evaluacion;
import itch.ac.model.Inscripcion;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {
	Evaluacion findByInscripcion(Inscripcion inscripcion);

	Evaluacion findByInscripcion_Id(Integer inscripcionId);
}