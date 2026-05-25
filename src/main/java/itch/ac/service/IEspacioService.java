package itch.ac.service;

import java.util.List;
import itch.ac.model.Espacio;

public interface IEspacioService {
	List<Espacio> buscarTodosEspacios();

	void guardarEspacio(Espacio espacio);

	Espacio buscarPorId(Integer id);

	Espacio eliminarPorId(Integer id);

	boolean existePorNombre(String nombre, Integer idExcluir);
}