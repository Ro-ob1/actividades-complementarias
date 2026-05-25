package itch.ac.service;

import java.util.List;
import itch.ac.model.Constancia;
import itch.ac.model.Inscripcion;

public interface IConstanciaService {
	List<Constancia> buscarTodasConstancias();

	Constancia buscarPorInscripcion(Inscripcion inscripcion);

	boolean existeConstancia(Inscripcion inscripcion);

	boolean existeConstanciaPorInscripcionId(Integer inscripcionId);

	boolean tieneEvaluacionCompleta(Inscripcion inscripcion);

	void guardarConstancia(Constancia constancia);

	Constancia buscarPorId(Integer id);

	Constancia eliminarPorId(Integer id);
}