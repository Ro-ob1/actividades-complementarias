package itch.ac.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import itch.ac.model.Actividad;
import itch.ac.model.Sesion;
import itch.ac.model.Horario;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {
	List<Sesion> findByHorario(Horario horario);

	List<Sesion> findByFecha(LocalDate fecha);

	List<Sesion> findByFechaBetween(LocalDate inicio, LocalDate fin);

	List<Sesion> findByHorario_ActividadOrderByFechaAsc(Actividad actividad);

	List<Sesion> findByHorario_Actividad_IdOrderByFechaAsc(Integer actividadId);

	boolean existsByHorario_IdAndFecha(Integer horarioId, LocalDate fecha);

	@Query("SELECT COUNT(s) FROM Sesion s " +
		   "WHERE s.horario.actividad.id = :actividadId " +
		   "AND s.estatus = 'REALIZADA'")
	long contarSesionesRealizadas(@Param("actividadId") Integer actividadId);

	@Query("SELECT COUNT(s) FROM Sesion s " +
		   "WHERE s.horario.actividad.id = :actividadId " +
		   "AND s.estatus <> 'CANCELADA'")
	long contarSesionesNoCanceladas(@Param("actividadId") Integer actividadId);
}