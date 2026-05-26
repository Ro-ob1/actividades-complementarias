package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Instructor;
import itch.ac.model.Persona;
import itch.ac.model.Usuario;
import itch.ac.repository.InstructorRepository;
import itch.ac.repository.UsuarioRepository;
import itch.ac.service.IInstructorService;

@Primary
@Service
public class InstructorServiceJPA implements IInstructorService {

	@Autowired
	private InstructorRepository instructorRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public List<Instructor> buscarTodosInstructores() {
		return instructorRepository.findAll();
	}

	@Override
	public List<Instructor> buscarInstructoresActivos() {
		return instructorRepository.findByActivo(1);
	}

	@Override
	public void guardarInstructor(Instructor instructor) {
		instructorRepository.save(instructor);
	}

	@Override
	public Instructor buscarPorId(Integer id) {
		Optional<Instructor> optional = instructorRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Instructor eliminarPorId(Integer id) {
		Optional<Instructor> optional = instructorRepository.findById(id);
		if (optional.isPresent()) {
			Instructor instructor = optional.get();
			instructor.setActivo(0);
			instructorRepository.save(instructor);
			Usuario usuario = usuarioRepository.findByPersona(instructor.getPersona());
			if (usuario != null && !"ROLE_ADMIN".equals(usuario.getRol())) {
				usuario.setActivo(0);
				usuarioRepository.save(usuario);
			}
			return instructor;
		}
		return null;
	}

	@Override
	public Instructor buscarPorPersona(Persona persona) {
		return instructorRepository.findByPersona(persona);
	}

	@Override
	public List<Instructor> buscarActivosPorNombre(String nombre) {
		return instructorRepository.findActivosByNombre(nombre);
	}
}