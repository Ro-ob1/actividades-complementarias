package itch.ac.service;

import java.util.List;
import java.time.LocalDate;
import itch.ac.model.Actividad;
import itch.ac.model.Sesion;
import itch.ac.model.Horario;

public interface ISesionService {
	List<Sesion> buscarTodasSesiones();

	List<Sesion> buscarSesionesPorHorario(Horario horario);

	List<Sesion> buscarSesionesPorActividad(Actividad actividad);

	List<Sesion> buscarSesionesPorActividadId(Integer actividadId);

	long contarSesionesRealizadas(Integer actividadId);

	long contarSesionesNoCanceladas(Integer actividadId);

	List<Sesion> buscarSesionesPorFecha(LocalDate fecha);

	List<Sesion> buscarSesionesPorRangoFecha(LocalDate inicio, LocalDate fin);

	void generarSesiones(Actividad actividad, List<Horario> horarios);

	void guardarSesion(Sesion sesion);

	Sesion buscarPorId(Integer id);

	Sesion eliminarPorId(Integer id);
}