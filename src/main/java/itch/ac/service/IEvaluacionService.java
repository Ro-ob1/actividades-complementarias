package itch.ac.service;

import java.util.List;
import itch.ac.model.Evaluacion;
import itch.ac.model.Inscripcion;

public interface IEvaluacionService {
	List<Evaluacion> buscarTodasEvaluaciones();

	Evaluacion buscarPorInscripcion(Inscripcion inscripcion);

	boolean inscripcionEstaAprobada(Inscripcion inscripcion);

	boolean existeEvaluacion(Inscripcion inscripcion);

	boolean existeEvaluacionPorInscripcionId(Integer inscripcionId);

	Evaluacion buscarPorInscripcionId(Integer inscripcionId);

	void guardarEvaluacion(Evaluacion evaluacion);

	Evaluacion buscarPorId(Integer id);

	Evaluacion eliminarPorId(Integer id);
}