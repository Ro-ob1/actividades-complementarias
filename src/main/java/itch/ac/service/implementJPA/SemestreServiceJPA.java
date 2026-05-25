package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Semestre;
import itch.ac.repository.SemestreRepository;
import itch.ac.service.ISemestreService;

@Primary
@Service
public class SemestreServiceJPA implements ISemestreService {

	@Autowired
	private SemestreRepository semestreRepository;

	@Override
	public List<Semestre> buscarTodosSemestres() {
		return semestreRepository.findAll();
	}

	@Override
	public void guardarSemestre(Semestre semestre) {
		semestreRepository.save(semestre);
	}

	@Override
	public Semestre buscarPorId(Integer id) {
		Optional<Semestre> optional = semestreRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Semestre eliminarPorId(Integer id) {
		Optional<Semestre> optional = semestreRepository.findById(id);
		if (optional.isPresent()) {
			Semestre semestre = optional.get();
			semestre.setActivo(0);
			semestreRepository.save(semestre);
			return semestre;
		}
		return null;
	}

	@Override
	public Semestre buscarSemestreActivo() {
		return semestreRepository.findByActivo(1);
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
		return semestreRepository.findAll().stream().filter(s -> idExcluir == null || !s.getId().equals(idExcluir))
				.anyMatch(s -> s.getNombre().equalsIgnoreCase(nombre.trim()));
	}
}