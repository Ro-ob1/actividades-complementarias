package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Evaluacion;
import itch.ac.model.Inscripcion;
import itch.ac.repository.EvaluacionRepository;
import itch.ac.service.IEvaluacionService;

@Primary
@Service
public class EvaluacionServiceJPA implements IEvaluacionService {

	@Autowired
	private EvaluacionRepository evaluacionRepository;

	@Override
	public List<Evaluacion> buscarTodasEvaluaciones() {
		return evaluacionRepository.findAll();
	}

	@Override
	public Evaluacion buscarPorInscripcion(Inscripcion inscripcion) {
		return evaluacionRepository.findByInscripcion(inscripcion);
	}

	@Override
	public boolean inscripcionEstaAprobada(Inscripcion inscripcion) {
		return inscripcion != null && "APROBADA".equals(inscripcion.getEstatusSolicitud());
	}

	@Override
	public boolean existeEvaluacion(Inscripcion inscripcion) {
		return evaluacionRepository.findByInscripcion(inscripcion) != null;
	}

	@Override
	public boolean existeEvaluacionPorInscripcionId(Integer inscripcionId) {
		return evaluacionRepository.findByInscripcion_Id(inscripcionId) != null;
	}

	@Override
	public Evaluacion buscarPorInscripcionId(Integer inscripcionId) {
		return evaluacionRepository.findByInscripcion_Id(inscripcionId);
	}

	@Override
	public void guardarEvaluacion(Evaluacion evaluacion) {
		evaluacionRepository.save(evaluacion);
	}

	@Override
	public Evaluacion buscarPorId(Integer id) {
		Optional<Evaluacion> optional = evaluacionRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Evaluacion eliminarPorId(Integer id) {
		Optional<Evaluacion> optional = evaluacionRepository.findById(id);
		if (optional.isPresent()) {
			Evaluacion evaluacion = optional.get();
			evaluacionRepository.delete(evaluacion);
			return evaluacion;
		}
		return null;
	}
}