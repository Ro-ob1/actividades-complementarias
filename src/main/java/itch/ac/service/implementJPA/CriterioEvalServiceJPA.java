package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.CriterioEval;
import itch.ac.model.Evaluacion;
import itch.ac.repository.CriterioEvalRepository;
import itch.ac.service.ICriterioEvalService;

@Primary
@Service
public class CriterioEvalServiceJPA implements ICriterioEvalService {

	@Autowired
	private CriterioEvalRepository criterioEvalRepository;

	@Override
	public List<CriterioEval> buscarTodosCriteriosEval() {
		return criterioEvalRepository.findAll();
	}

	@Override
	public List<CriterioEval> buscarCriteriosPorEvaluacion(Evaluacion evaluacion) {
		return criterioEvalRepository.findByEvaluacion(evaluacion);
	}

	@Override
	public List<CriterioEval> buscarCriteriosPorEvaluacionId(Integer evaluacionId) {
		return criterioEvalRepository.findByEvaluacion_Id(evaluacionId);
	}

	@Override
	public void guardarCriterioEval(CriterioEval criterioEval) {
		criterioEvalRepository.save(criterioEval);
	}

	@Override
	public CriterioEval buscarPorId(Integer id) {
		Optional<CriterioEval> optional = criterioEvalRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public CriterioEval eliminarPorId(Integer id) {
		Optional<CriterioEval> optional = criterioEvalRepository.findById(id);
		if (optional.isPresent()) {
			CriterioEval criterioEval = optional.get();
			criterioEvalRepository.delete(criterioEval);
			return criterioEval;
		}
		return null;
	}
}