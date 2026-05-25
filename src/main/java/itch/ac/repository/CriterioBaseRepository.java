package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.CriterioBase;

public interface CriterioBaseRepository extends JpaRepository<CriterioBase, Integer> {
	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNumeroCriterioAndIdNot(Integer numeroCriterio, Integer id);
}