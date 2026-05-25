package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Espacio;
import itch.ac.repository.EspacioRepository;
import itch.ac.service.IEspacioService;

@Primary
@Service
public class EspacioServiceJPA implements IEspacioService {

	@Autowired
	private EspacioRepository espacioRepository;

	@Override
	public List<Espacio> buscarTodosEspacios() {
		return espacioRepository.findAll();
	}

	@Override
	public void guardarEspacio(Espacio espacio) {
		espacioRepository.save(espacio);
	}

	@Override
	public Espacio buscarPorId(Integer id) {
		Optional<Espacio> optional = espacioRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Espacio eliminarPorId(Integer id) {
		Optional<Espacio> optional = espacioRepository.findById(id);
		if (optional.isPresent()) {
			Espacio espacio = optional.get();
			espacioRepository.delete(espacio);
			return espacio;
		}
		return null;
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
		return espacioRepository.findAll().stream().filter(e -> idExcluir == null || !e.getId().equals(idExcluir))
				.anyMatch(e -> e.getNombre().equalsIgnoreCase(nombre.trim()));
	}
}