package itch.ac.service;

import java.util.List;
import itch.ac.model.Carrera;

public interface ICarreraService {
	List<Carrera> buscarTodasCarreras();

	void guardarCarrera(Carrera carrera);

	Carrera buscarPorId(Integer id);

	Carrera eliminarPorId(Integer id);

	boolean existePorClave(String clave, Integer idExcluir);

	boolean existePorNombre(String nombre, Integer idExcluir);
}