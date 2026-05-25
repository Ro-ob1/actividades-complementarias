package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Constancia;
import itch.ac.model.Inscripcion;
import itch.ac.repository.ConstanciaRepository;
import itch.ac.repository.EvaluacionRepository;
import itch.ac.service.IConstanciaService;

@Primary
@Service
public class ConstanciaServiceJPA implements IConstanciaService {

	@Autowired
	private ConstanciaRepository constanciaRepository;

	@Autowired
	private EvaluacionRepository evaluacionRepository;

	@Override
	public List<Constancia> buscarTodasConstancias() {
		return constanciaRepository.findAll();
	}

	@Override
	public Constancia buscarPorInscripcion(Inscripcion inscripcion) {
		return constanciaRepository.findByInscripcion(inscripcion);
	}

	@Override
	public boolean existeConstancia(Inscripcion inscripcion) {
		return constanciaRepository.findByInscripcion(inscripcion) != null;
	}

	@Override
	public boolean existeConstanciaPorInscripcionId(Integer inscripcionId) {
		return constanciaRepository.findByInscripcion_Id(inscripcionId) != null;
	}

	@Override
	public boolean tieneEvaluacionCompleta(Inscripcion inscripcion) {
		return evaluacionRepository.findByInscripcion(inscripcion) != null;
	}

	@Override
	public void guardarConstancia(Constancia constancia) {
		constanciaRepository.save(constancia);
	}

	@Override
	public Constancia buscarPorId(Integer id) {
		Optional<Constancia> optional = constanciaRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Constancia eliminarPorId(Integer id) {
		Optional<Constancia> optional = constanciaRepository.findById(id);
		if (optional.isPresent()) {
			Constancia constancia = optional.get();
			constanciaRepository.delete(constancia);
			return constancia;
		}
		return null;
	}
}