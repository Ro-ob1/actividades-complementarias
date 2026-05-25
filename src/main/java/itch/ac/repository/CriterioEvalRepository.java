package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.CriterioEval;
import itch.ac.model.Evaluacion;

public interface CriterioEvalRepository extends JpaRepository<CriterioEval, Integer> {
	List<CriterioEval> findByEvaluacion(Evaluacion evaluacion);
	List<CriterioEval> findByEvaluacion_Id(Integer evaluacionId);
}