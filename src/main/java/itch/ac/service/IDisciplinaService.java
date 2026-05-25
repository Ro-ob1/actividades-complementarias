package itch.ac.service;

import java.util.List;
import itch.ac.model.Disciplina;

public interface IDisciplinaService {
	List<Disciplina> buscarTodasDisciplinas();

	void guardarDisciplina(Disciplina disciplina);

	Disciplina buscarPorId(Integer id);

	Disciplina eliminarPorId(Integer id);
	
	boolean existePorNombre(String nombre, Integer idExcluir);
}