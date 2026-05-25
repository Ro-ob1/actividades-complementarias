package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import itch.ac.model.Asistencia;
import itch.ac.model.Alumno;
import itch.ac.model.Sesion;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {
	List<Asistencia> findByAlumno(Alumno alumno);

	List<Asistencia> findByAlumno_Id(Integer alumnoId);

	List<Asistencia> findBySesion(Sesion sesion);

	Asistencia findByAlumnoAndSesion(Alumno alumno, Sesion sesion);

	@Query("SELECT COUNT(a) FROM Asistencia a " +
		   "WHERE a.alumno.id = :alumnoId " +
		   "AND a.sesion.horario.actividad.id = :actividadId " +
		   "AND a.sesion.estatus = 'REALIZADA' " +
		   "AND a.asistio = true")
	long contarAsistenciasRealizadas(@Param("alumnoId") Integer alumnoId,
									 @Param("actividadId") Integer actividadId);

	@Query("SELECT COUNT(a) FROM Asistencia a " +
		   "WHERE a.alumno.id = :alumnoId " +
		   "AND a.sesion.horario.actividad.id = :actividadId " +
		   "AND a.sesion.estatus <> 'CANCELADA' " +
		   "AND a.asistio = true")
	long contarAsistenciasNoCanceladas(@Param("alumnoId") Integer alumnoId,
									   @Param("actividadId") Integer actividadId);
}