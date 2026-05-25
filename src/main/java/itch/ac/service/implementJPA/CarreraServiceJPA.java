package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Carrera;
import itch.ac.repository.CarreraRepository;
import itch.ac.service.ICarreraService;

@Primary
@Service
public class CarreraServiceJPA implements ICarreraService {

	@Autowired
	private CarreraRepository carreraRepository;

	@Override
	public List<Carrera> buscarTodasCarreras() {
		return carreraRepository.findAll();
	}

	@Override
	public void guardarCarrera(Carrera carrera) {
		carreraRepository.save(carrera);
	}

	@Override
	public Carrera buscarPorId(Integer id) {
		Optional<Carrera> optional = carreraRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Carrera eliminarPorId(Integer id) {
		Optional<Carrera> optional = carreraRepository.findById(id);
		if (optional.isPresent()) {
			Carrera carrera = optional.get();
			carreraRepository.delete(carrera);
			return carrera;
		}
		return null;
	}

	@Override
	public boolean existePorClave(String clave, Integer idExcluir) {
		return carreraRepository.findAll().stream().filter(c -> idExcluir == null || !c.getId().equals(idExcluir))
				.anyMatch(c -> c.getClave().equalsIgnoreCase(clave.trim()));
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
		return carreraRepository.findAll().stream().filter(c -> idExcluir == null || !c.getId().equals(idExcluir))
				.anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
	}
}