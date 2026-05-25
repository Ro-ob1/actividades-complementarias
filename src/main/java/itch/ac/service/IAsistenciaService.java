package itch.ac.service;

import java.util.List;

import itch.ac.model.Actividad;
import itch.ac.model.Alumno;
import itch.ac.model.Asistencia;
import itch.ac.model.Sesion;

public interface IAsistenciaService {
	List<Asistencia> buscarTodasAsistencias();

	List<Asistencia> buscarAsistenciasPorAlumno(Alumno alumno);

	List<Asistencia> buscarAsistenciasPorSesion(Sesion sesion);

	boolean existeAsistencia(Alumno alumno, Sesion sesion);

	void guardarAsistencia(Asistencia asistencia);

	Asistencia buscarPorId(Integer id);

	Asistencia eliminarPorId(Integer id);

	Asistencia buscarPorAlumnoYSesion(Alumno alumno, Sesion sesion);
	long contarAsistenciasPorAlumnoYActividad(Alumno alumno, Actividad actividad);

	long contarAsistenciasPorAlumnoIdYActividadId(Integer alumnoId, Integer actividadId);

	long contarAsistenciasRealizadas(Integer alumnoId, Integer actividadId);

	long contarAsistenciasNoCanceladas(Integer alumnoId, Integer actividadId);
}