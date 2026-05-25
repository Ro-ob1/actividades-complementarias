package itch.ac.service;

import java.util.List;

import itch.ac.model.Alumno;
import itch.ac.model.Persona;

public interface IAlumnoService {
	List<Alumno> buscarTodosAlumnos();

	List<Alumno> buscarAlumnosActivos();

	void guardarAlumno(Alumno alumno);

	Alumno buscarPorId(Integer id);

	Alumno buscarPorNumControl(String numControl);

	Alumno eliminarPorId(Integer id);

	boolean existePorNumControl(String numControl, Integer idExcluir);

	Alumno buscarPorPersona(Persona persona);

	List<Alumno> buscarActivosPorNombre(String nombre);
}