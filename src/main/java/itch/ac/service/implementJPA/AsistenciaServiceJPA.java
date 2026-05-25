package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Actividad;
import itch.ac.model.Alumno;
import itch.ac.model.Asistencia;
import itch.ac.model.Sesion;
import itch.ac.repository.AsistenciaRepository;
import itch.ac.service.IAsistenciaService;

@Primary
@Service
public class AsistenciaServiceJPA implements IAsistenciaService {

	@Autowired
	private AsistenciaRepository asistenciaRepository;

	@Override
	public List<Asistencia> buscarTodasAsistencias() {
		return asistenciaRepository.findAll();
	}

	@Override
	public List<Asistencia> buscarAsistenciasPorAlumno(Alumno alumno) {
		return asistenciaRepository.findByAlumno(alumno);
	}

	@Override
	public List<Asistencia> buscarAsistenciasPorSesion(Sesion sesion) {
		return asistenciaRepository.findBySesion(sesion);
	}

	@Override
	public boolean existeAsistencia(Alumno alumno, Sesion sesion) {
		return asistenciaRepository.findByAlumnoAndSesion(alumno, sesion) != null;
	}

	@Override
	public void guardarAsistencia(Asistencia asistencia) {
		asistenciaRepository.save(asistencia);
	}

	@Override
	public Asistencia buscarPorId(Integer id) {
		Optional<Asistencia> optional = asistenciaRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Asistencia eliminarPorId(Integer id) {
		Optional<Asistencia> optional = asistenciaRepository.findById(id);
		if (optional.isPresent()) {
			Asistencia asistencia = optional.get();
			asistenciaRepository.delete(asistencia);
			return asistencia;
		}
		return null;
	}

	@Override
	public Asistencia buscarPorAlumnoYSesion(Alumno alumno, Sesion sesion) {
		return asistenciaRepository.findByAlumnoAndSesion(alumno, sesion);
	}

	@Override
	public long contarAsistenciasPorAlumnoYActividad(Alumno alumno, Actividad actividad) {
		return asistenciaRepository.findByAlumno(alumno).stream()
				.filter(a -> a.getSesion().getHorario().getActividad().getId().equals(actividad.getId()))
				.filter(a -> !"CANCELADA".equals(a.getSesion().getEstatus()))
				.filter(a -> Boolean.TRUE.equals(a.getAsistio())).count();
	}

	@Override
	public long contarAsistenciasPorAlumnoIdYActividadId(Integer alumnoId, Integer actividadId) {
		return asistenciaRepository.findByAlumno_Id(alumnoId).stream()
				.filter(a -> a.getSesion().getHorario().getActividad().getId().equals(actividadId))
				.filter(a -> !"CANCELADA".equals(a.getSesion().getEstatus()))
				.filter(a -> Boolean.TRUE.equals(a.getAsistio())).count();
	}

	@Override
	public long contarAsistenciasRealizadas(Integer alumnoId, Integer actividadId) {
		return asistenciaRepository.contarAsistenciasRealizadas(alumnoId, actividadId);
	}

	@Override
	public long contarAsistenciasNoCanceladas(Integer alumnoId, Integer actividadId) {
		return asistenciaRepository.contarAsistenciasNoCanceladas(alumnoId, actividadId);
	}
}