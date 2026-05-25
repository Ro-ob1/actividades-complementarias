package itch.ac.service;

import java.time.LocalTime;
import java.util.List;
import itch.ac.model.Horario;
import itch.ac.model.Actividad;
import itch.ac.model.Instructor;
import itch.ac.model.Semestre;

public interface IHorarioService {
	List<Horario> buscarTodosHorarios();

	List<Horario> buscarHorariosPorActividad(Actividad actividad);

	List<Horario> buscarHorariosPorInstructorYSemestre(Instructor instructor, Semestre semestre);

	void guardarHorario(Horario horario);

	Horario buscarPorId(Integer id);

	Horario eliminarPorId(Integer id);

	List<Horario> buscarConflictos(Integer espacioId, String dia, LocalTime inicio, LocalTime fin, Integer idExcluir, Integer idSemestre);
}