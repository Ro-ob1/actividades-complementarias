package itch.ac.service;

import java.util.List;

import itch.ac.model.Instructor;
import itch.ac.model.Persona;

public interface IInstructorService {
	List<Instructor> buscarTodosInstructores();

	List<Instructor> buscarInstructoresActivos();

	void guardarInstructor(Instructor instructor);

	Instructor buscarPorId(Integer id);

	Instructor eliminarPorId(Integer id);

	Instructor buscarPorPersona(Persona persona);

	List<Instructor> buscarActivosPorNombre(String nombre);
}