package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Alumno;
import itch.ac.model.Persona;
import itch.ac.repository.AlumnoRepository;
import itch.ac.service.IAlumnoService;

@Primary
@Service
public class AlumnoServiceJPA implements IAlumnoService {

	@Autowired
	private AlumnoRepository alumnoRepository;

	@Override
	public List<Alumno> buscarTodosAlumnos() {
		return alumnoRepository.findAll();
	}

	@Override
	public List<Alumno> buscarAlumnosActivos() {
		return alumnoRepository.findByActivo(1);
	}

	@Override
	public void guardarAlumno(Alumno alumno) {
		alumnoRepository.save(alumno);
	}

	@Override
	public Alumno buscarPorId(Integer id) {
		Optional<Alumno> optional = alumnoRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Alumno buscarPorNumControl(String numControl) {
		return alumnoRepository.findByNumControl(numControl);
	}

	@Override
	public Alumno eliminarPorId(Integer id) {
		Optional<Alumno> optional = alumnoRepository.findById(id);
		if (optional.isPresent()) {
			Alumno alumno = optional.get();
			alumno.setActivo(0);
			alumnoRepository.save(alumno);
			return alumno;
		}
		return null;
	}

	@Override
	public boolean existePorNumControl(String numControl, Integer idExcluir) {
		return alumnoRepository.findAll().stream().filter(a -> idExcluir == null || !a.getId().equals(idExcluir))
				.anyMatch(a -> a.getNumControl().equalsIgnoreCase(numControl.trim()));
	}

	@Override
	public Alumno buscarPorPersona(Persona persona) {
		return alumnoRepository.findByPersona(persona);
	}

	@Override
	public List<Alumno> buscarActivosPorNombre(String nombre) {
		return alumnoRepository.findActivosByNombre(nombre);
	}
}