package itch.ac.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import itch.ac.model.Horario;
import itch.ac.model.Actividad;
import itch.ac.model.Instructor;
import itch.ac.model.Semestre;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {
	List<Horario> findByActividad(Actividad actividad);

	List<Horario> findByActividadInstructorAndActividadSemestre(Instructor instructor, Semestre semestre);

	@Query("SELECT h FROM Horario h WHERE h.espacio.id = :espacioId AND h.diaSemana = :dia " +
	       "AND :inicio < h.horaFin AND :fin > h.horaInicio")
	List<Horario> findConflictos(@Param("espacioId") Integer espacioId,
	                             @Param("dia") String dia,
	                             @Param("inicio") LocalTime inicio,
	                             @Param("fin") LocalTime fin);
}