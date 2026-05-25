package itch.ac.service;

import java.util.List;
import itch.ac.model.Actividad;
import itch.ac.model.Semestre;

public interface IActividadService {
	List<Actividad> buscarTodasActividades();

	List<Actividad> buscarActividadesActivas();

	List<Actividad> buscarActividadesPorSemestre(Semestre semestre);

	List<Actividad> buscarActividadesActivasPorSemestre(Semestre semestre);

	List<Actividad> buscarPorNombre(String nombre);

	boolean existePorNombre(String nombre, Integer idExcluir, Integer idSemestre);

	void desactivarActividadesPorSemestre(Semestre semestre);

	void guardarActividad(Actividad actividad);

	Actividad buscarPorId(Integer id);

	Actividad eliminarPorId(Integer id);
}