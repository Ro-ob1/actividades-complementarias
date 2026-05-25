package itch.ac.service;

import java.util.List;
import itch.ac.model.CriterioBase;

public interface ICriterioBaseService {
	List<CriterioBase> buscarTodosCriterios();

	void guardarCriterio(CriterioBase criterio);

	CriterioBase buscarPorId(Integer id);

	CriterioBase eliminarPorId(Integer id);

	boolean existePorNombre(String nombre, Integer idExcluir);

	boolean existePorNumero(Integer numero, Integer idExcluir);
}