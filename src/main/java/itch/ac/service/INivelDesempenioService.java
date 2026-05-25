package itch.ac.service;

import java.util.List;
import itch.ac.model.NivelDesempenio;

public interface INivelDesempenioService {
	List<NivelDesempenio> buscarTodosNiveles();

	void guardarNivel(NivelDesempenio nivel);

	NivelDesempenio buscarPorId(Integer id);

	NivelDesempenio eliminarPorId(Integer id);

	NivelDesempenio buscarPorValor(Double valor);

	boolean existePorNombre(String nombre, Integer idExcluir);
}