package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Disciplina;
import itch.ac.repository.DisciplinaRepository;
import itch.ac.service.IDisciplinaService;

@Primary
@Service
public class DisciplinaServiceJPA implements IDisciplinaService {

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Override
	public List<Disciplina> buscarTodasDisciplinas() {
		return disciplinaRepository.findAll();
	}

	@Override
	public void guardarDisciplina(Disciplina disciplina) {
		disciplinaRepository.save(disciplina);
	}

	@Override
	public Disciplina buscarPorId(Integer id) {
		Optional<Disciplina> optional = disciplinaRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Disciplina eliminarPorId(Integer id) {
		Optional<Disciplina> optional = disciplinaRepository.findById(id);
		if (optional.isPresent()) {
			Disciplina disciplina = optional.get();
			disciplinaRepository.delete(disciplina);
			return disciplina;
		}
		return null;
	}
	
	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir) {
	    return disciplinaRepository.findAll().stream()
	        .filter(d -> idExcluir == null || !d.getId().equals(idExcluir))
	        .anyMatch(d -> d.getNombre().equalsIgnoreCase(nombre.trim()));
	}
}