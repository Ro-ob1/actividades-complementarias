package itch.ac.service.implementJPA;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Actividad;
import itch.ac.model.Horario;
import itch.ac.model.Instructor;
import itch.ac.model.Semestre;
import itch.ac.repository.HorarioRepository;
import itch.ac.service.IHorarioService;

@Primary
@Service
public class HorarioServiceJPA implements IHorarioService {

	@Autowired
	private HorarioRepository horarioRepository;

	@Override
	public List<Horario> buscarTodosHorarios() {
		return horarioRepository.findAll();
	}

	@Override
	public List<Horario> buscarHorariosPorActividad(Actividad actividad) {
		return horarioRepository.findByActividad(actividad);
	}

	@Override
	public List<Horario> buscarHorariosPorInstructorYSemestre(Instructor instructor, Semestre semestre) {
		return horarioRepository.findByActividadInstructorAndActividadSemestre(instructor, semestre);
	}

	@Override
	public void guardarHorario(Horario horario) {
		horarioRepository.save(horario);
	}

	@Override
	public Horario buscarPorId(Integer id) {
		Optional<Horario> optional = horarioRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Horario eliminarPorId(Integer id) {
		Optional<Horario> optional = horarioRepository.findById(id);
		if (optional.isPresent()) {
			Horario horario = optional.get();
			horarioRepository.delete(horario);
			return horario;
		}
		return null;
	}

	@Override
	public List<Horario> buscarConflictos(Integer espacioId, String dia, LocalTime inicio, LocalTime fin, Integer idExcluir, Integer idSemestre) {
		return horarioRepository.findConflictos(espacioId, dia, inicio, fin)
			.stream()
			.filter(h -> idExcluir == null || !h.getId().equals(idExcluir))
			.filter(h -> idSemestre == null
				|| (h.getActividad().getSemestre() != null
					&& h.getActividad().getSemestre().getId().equals(idSemestre)))
			.filter(h -> Integer.valueOf(1).equals(h.getActividad().getActivo()))
			.collect(Collectors.toList());
	}
}