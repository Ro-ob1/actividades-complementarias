package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Encargado;
import itch.ac.model.Persona;
import itch.ac.repository.EncargadoRepository;
import itch.ac.service.IEncargadoService;

@Primary
@Service
public class EncargadoServiceJPA implements IEncargadoService {

	@Autowired
	private EncargadoRepository encargadoRepository;

	@Override
	public List<Encargado> buscarTodosEncargados() {
		return encargadoRepository.findAll();
	}

	@Override
	public List<Encargado> buscarEncargadosActivos() {
		return encargadoRepository.findByActivo(1);
	}

	@Override
	public void guardarEncargado(Encargado encargado) {
		encargadoRepository.save(encargado);
	}

	@Override
	public Encargado buscarPorId(Integer id) {
		Optional<Encargado> optional = encargadoRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Encargado eliminarPorId(Integer id) {
		Optional<Encargado> optional = encargadoRepository.findById(id);
		if (optional.isPresent()) {
			Encargado encargado = optional.get();
			encargado.setActivo(0);
			encargadoRepository.save(encargado);
			return encargado;
		}
		return null;
	}

	@Override
	public Encargado buscarPorPersona(Persona persona) {
		return encargadoRepository.findByPersona(persona);
	}
}