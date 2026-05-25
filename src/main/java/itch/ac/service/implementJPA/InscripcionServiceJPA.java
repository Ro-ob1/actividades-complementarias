package itch.ac.service.implementJPA;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Actividad;
import itch.ac.model.Alumno;
import itch.ac.model.Inscripcion;
import itch.ac.model.Semestre;
import itch.ac.repository.InscripcionRepository;
import itch.ac.service.IInscripcionService;

@Primary
@Service
public class InscripcionServiceJPA implements IInscripcionService {

	@Autowired
	private InscripcionRepository inscripcionRepository;

	@Override
	public List<Inscripcion> buscarTodasInscripciones() {
		return inscripcionRepository.findAll();
	}

	@Override
	public List<Inscripcion> buscarInscripcionesPorAlumno(Alumno alumno) {
		return inscripcionRepository.findByAlumno(alumno);
	}

	@Override
	public List<Inscripcion> buscarInscripcionesPorActividad(Actividad actividad) {
		return inscripcionRepository.findByActividad(actividad);
	}

	@Override
	public List<Inscripcion> buscarInscripcionesPorEstatus(String estatus) {
		return inscripcionRepository.findByEstatusSolicitud(estatus);
	}

	@Override
	public List<Inscripcion> buscarInscripcionesPorAlumnoYEstatus(Alumno alumno, String estatus) {
		return inscripcionRepository.findByAlumnoAndEstatusSolicitud(alumno, estatus);
	}

	@Override
	public List<Inscripcion> buscarInscripcionesPorSemestre(Semestre semestre) {
		return inscripcionRepository.findByActividadSemestre(semestre);
	}

	@Override
	public Inscripcion buscarInscripcionActivaPorAlumnoYSemestre(Alumno alumno, Semestre semestre) {
		if (alumno == null || alumno.getId() == null || semestre == null || semestre.getId() == null) return null;
		return inscripcionRepository.findByAlumno_IdAndActividad_Semestre_IdAndEstatusSolicitudIn(
				alumno.getId(), semestre.getId(), Arrays.asList("PENDIENTE", "APROBADA"));
	}

	@Override
	public boolean alumnoTieneInscripcionActiva(Alumno alumno, Semestre semestre) {
		Inscripcion inscripcion = buscarInscripcionActivaPorAlumnoYSemestre(alumno, semestre);
		return inscripcion != null;
	}

	@Override
	public boolean actividadTieneCupoDisponible(Actividad actividad) {
	    if (actividad.getCupoMaximo() == null) {
	        return true;
	    }
	    long inscritos = inscripcionRepository.findByActividad(actividad)
	        .stream()
	        .filter(i -> "APROBADA".equals(i.getEstatusSolicitud()))
	        .count();
	    return inscritos < actividad.getCupoMaximo();
	}
	
	@Override
	public void guardarInscripcion(Inscripcion inscripcion) {
	    if (inscripcion.getId() == null) {
	        inscripcion.setFechaSolicitud(LocalDate.now());
	        if (inscripcion.getEstatusSolicitud() == null) {
	            inscripcion.setEstatusSolicitud("PENDIENTE");
	        }
	    }
	    inscripcionRepository.save(inscripcion);
	}

	@Override
	public Inscripcion buscarPorId(Integer id) {
		Optional<Inscripcion> optional = inscripcionRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Inscripcion eliminarPorId(Integer id) {
		Optional<Inscripcion> optional = inscripcionRepository.findById(id);
		if (optional.isPresent()) {
			Inscripcion inscripcion = optional.get();
			inscripcionRepository.delete(inscripcion);
			return inscripcion;
		}
		return null;
	}
}