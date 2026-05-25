package itch.ac.service;

import java.util.List;
import itch.ac.model.Semestre;

public interface ISemestreService {
	List<Semestre> buscarTodosSemestres();

	void guardarSemestre(Semestre semestre);

	Semestre buscarPorId(Integer id);

	Semestre eliminarPorId(Integer id);

	Semestre buscarSemestreActivo();

	boolean existePorNombre(String nombre, Integer idExcluir);
}
