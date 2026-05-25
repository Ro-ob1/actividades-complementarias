package itch.ac.service;

import java.util.List;
import itch.ac.model.Persona;

public interface IPersonaService {
	List<Persona> buscarTodasPersonas();

	void guardarPersona(Persona persona);

	Persona buscarPorId(Integer id);

	Persona eliminarPorId(Integer id);

	boolean existePorEmail(String email, Integer idExcluir);
}