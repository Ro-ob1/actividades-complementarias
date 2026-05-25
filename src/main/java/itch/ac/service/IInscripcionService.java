package itch.ac.service;

import java.util.List;
import itch.ac.model.Inscripcion;
import itch.ac.model.Alumno;
import itch.ac.model.Actividad;
import itch.ac.model.Semestre;

public interface IInscripcionService {
	List<Inscripcion> buscarTodasInscripciones();

	List<Inscripcion> buscarInscripcionesPorAlumno(Alumno alumno);

	List<Inscripcion> buscarInscripcionesPorActividad(Actividad actividad);

	List<Inscripcion> buscarInscripcionesPorEstatus(String estatus);

	List<Inscripcion> buscarInscripcionesPorAlumnoYEstatus(Alumno alumno, String estatus);

	List<Inscripcion> buscarInscripcionesPorSemestre(Semestre semestre);

	Inscripcion buscarInscripcionActivaPorAlumnoYSemestre(Alumno alumno, Semestre semestre);

	boolean alumnoTieneInscripcionActiva(Alumno alumno, Semestre semestre);

	boolean actividadTieneCupoDisponible(Actividad actividad);

	void guardarInscripcion(Inscripcion inscripcion);

	Inscripcion buscarPorId(Integer id);

	Inscripcion eliminarPorId(Integer id);
}