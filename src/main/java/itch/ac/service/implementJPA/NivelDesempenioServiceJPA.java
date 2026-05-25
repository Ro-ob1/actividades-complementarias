package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.NivelDesempenio;
import itch.ac.repository.NivelDesempenioRepository;
import itch.ac.service.INivelDesempenioService;

@Primary
@Service
public class NivelDesempenioServiceJPA implements INivelDesempenioService {

	@Autowired
	private NivelDesempenioRepository nivelDesempenioRepository;

	@Override
	public List<NivelDesempenio> buscarTodosNiveles() {
		return nivelDesempenioRepository.findAll();
	}

	@Override
	public void guardarNivel(NivelDesempenio nivel) {
		nivelDesempenioRepository.save(nivel);
	}

	@Override
	public NivelDesempenio buscarPorId(Integer id) {
		Optional<NivelDesempenio> optional = nivelDesempenioRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public NivelDesempenio eliminarPorId(Integer id) {
		Optional<NivelDesempenio> optional = nivelDesempenioRepository.findById(id);
		if (optional.isPresent()) {
			NivelDesempenio nivel = optional.get();
			nivelDesempenioRepository.delete(nivel);
			return nivel;
		}
		return null;
	}

	@Override
	public NivelDesempenio buscarPorValor(Double valor) {
		List<NivelDesempenio> niveles = nivelDesempenioRepository.findAll();
		for (NivelDesempenio nivel : niveles) {
			if (valor >= nivel.getRangoMin() && valor <= nivel.getRangoMax()) {
				return nivel;
			}
		}
		return null;
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
		return nivelDesempenioRepository.findAll().stream()
				.filter(n -> idExcluir == null || !n.getId().equals(idExcluir))
				.anyMatch(n -> n.getNombre().equalsIgnoreCase(nombre.trim()));
	}
}