package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
	boolean existsByNombreIgnoreCase(String nombre);
}