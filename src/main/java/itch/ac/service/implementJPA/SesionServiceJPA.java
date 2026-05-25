package itch.ac.service.implementJPA;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Actividad;
import itch.ac.model.Horario;
import itch.ac.model.Semestre;
import itch.ac.model.Sesion;
import itch.ac.repository.SesionRepository;
import itch.ac.service.ISesionService;

@Primary
@Service
public class SesionServiceJPA implements ISesionService {

	@Autowired
	private SesionRepository sesionRepository;

	private static final Map<String, DayOfWeek> DIA_MAP = Map.of(
		"LUNES",     DayOfWeek.MONDAY,
		"MARTES",    DayOfWeek.TUESDAY,
		"MIERCOLES", DayOfWeek.WEDNESDAY,
		"JUEVES",    DayOfWeek.THURSDAY,
		"VIERNES",   DayOfWeek.FRIDAY
	);

	@Override
	public List<Sesion> buscarTodasSesiones() {
		return sesionRepository.findAll();
	}

	@Override
	public List<Sesion> buscarSesionesPorHorario(Horario horario) {
		return sesionRepository.findByHorario(horario);
	}

	@Override
	public List<Sesion> buscarSesionesPorActividad(Actividad actividad) {
		return sesionRepository.findByHorario_ActividadOrderByFechaAsc(actividad);
	}

	@Override
	public List<Sesion> buscarSesionesPorActividadId(Integer actividadId) {
		return sesionRepository.findByHorario_Actividad_IdOrderByFechaAsc(actividadId);
	}

	@Override
	public long contarSesionesRealizadas(Integer actividadId) {
		return sesionRepository.contarSesionesRealizadas(actividadId);
	}

	@Override
	public long contarSesionesNoCanceladas(Integer actividadId) {
		return sesionRepository.contarSesionesNoCanceladas(actividadId);
	}

	@Override
	public List<Sesion> buscarSesionesPorFecha(LocalDate fecha) {
		return sesionRepository.findByFecha(fecha);
	}

	@Override
	public List<Sesion> buscarSesionesPorRangoFecha(LocalDate inicio, LocalDate fin) {
		return sesionRepository.findByFechaBetween(inicio, fin);
	}

	@Override
	public void generarSesiones(Actividad actividad, List<Horario> horarios) {
		Semestre semestre = actividad.getSemestre();
		if (semestre == null || semestre.getFechaInicio() == null || semestre.getFechaFin() == null) return;

		LocalDate fechaInicio = semestre.getFechaInicio();
		LocalDate fechaFin    = semestre.getFechaFin();

		for (Horario horario : horarios) {
			DayOfWeek dow = DIA_MAP.get(horario.getDiaSemana());
			if (dow == null) continue;

			// Avanzar hasta el primer día de la semana que corresponda
			LocalDate current = fechaInicio;
			while (current.getDayOfWeek() != dow) {
				current = current.plusDays(1);
			}

			while (!current.isAfter(fechaFin)) {
				if (!sesionRepository.existsByHorario_IdAndFecha(horario.getId(), current)) {
					Sesion sesion = new Sesion();
					sesion.setHorario(horario);
					sesion.setFecha(current);
					sesion.setEstatus("PROGRAMADA");
					sesionRepository.save(sesion);
				}
				current = current.plusWeeks(1);
			}
		}

		// Actualizar numeroSesion ordenado por fecha
		List<Sesion> todas = sesionRepository.findByHorario_ActividadOrderByFechaAsc(actividad);
		todas.sort(Comparator.comparing(Sesion::getFecha));
		for (int i = 0; i < todas.size(); i++) {
			todas.get(i).setNumeroSesion(i + 1);
			sesionRepository.save(todas.get(i));
		}
	}

	@Override
	public void guardarSesion(Sesion sesion) {
		sesionRepository.save(sesion);
	}

	@Override
	public Sesion buscarPorId(Integer id) {
		Optional<Sesion> optional = sesionRepository.findById(id);
		return optional.orElse(null);
	}

	@Override
	public Sesion eliminarPorId(Integer id) {
		Optional<Sesion> optional = sesionRepository.findById(id);
		if (optional.isPresent()) {
			Sesion sesion = optional.get();
			sesionRepository.delete(sesion);
			return sesion;
		}
		return null;
	}
}
