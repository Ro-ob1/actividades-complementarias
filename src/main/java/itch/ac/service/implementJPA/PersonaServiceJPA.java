package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Persona;
import itch.ac.repository.PersonaRepository;
import itch.ac.service.IPersonaService;

@Primary
@Service
public class PersonaServiceJPA implements IPersonaService {

	@Autowired
	private PersonaRepository personaRepository;

	@Override
	public List<Persona> buscarTodasPersonas() {
		return personaRepository.findAll();
	}

	@Override
	public void guardarPersona(Persona persona) {
		personaRepository.save(persona);
	}

	@Override
	public Persona buscarPorId(Integer id) {
		Optional<Persona> optional = personaRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Persona eliminarPorId(Integer id) {
		Optional<Persona> optional = personaRepository.findById(id);
		if (optional.isPresent()) {
			Persona persona = optional.get();
			personaRepository.delete(persona);
			return persona;
		}
		return null;
	}

	@Override
	public boolean existePorEmail(String email, Integer idExcluir) {
		return personaRepository.findAll().stream().filter(p -> idExcluir == null || !p.getId().equals(idExcluir))
				.anyMatch(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(email.trim()));
	}
}