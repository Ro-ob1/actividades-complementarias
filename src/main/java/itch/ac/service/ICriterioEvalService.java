package itch.ac.service;

import java.util.List;
import itch.ac.model.CriterioEval;
import itch.ac.model.Evaluacion;

public interface ICriterioEvalService {
	List<CriterioEval> buscarTodosCriteriosEval();

	List<CriterioEval> buscarCriteriosPorEvaluacion(Evaluacion evaluacion);

	List<CriterioEval> buscarCriteriosPorEvaluacionId(Integer evaluacionId);

	void guardarCriterioEval(CriterioEval criterioEval);

	CriterioEval buscarPorId(Integer id);

	CriterioEval eliminarPorId(Integer id);
}