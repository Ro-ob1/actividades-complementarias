package itch.ac.service;

import java.util.List;

import itch.ac.model.Encargado;
import itch.ac.model.Persona;

public interface IEncargadoService {
	List<Encargado> buscarTodosEncargados();

	List<Encargado> buscarEncargadosActivos();

	void guardarEncargado(Encargado encargado);

	Encargado buscarPorId(Integer id);

	Encargado eliminarPorId(Integer id);

	Encargado buscarPorPersona(Persona persona);
}