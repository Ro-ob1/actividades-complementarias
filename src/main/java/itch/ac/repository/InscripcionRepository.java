package itch.ac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Inscripcion;
import itch.ac.model.Alumno;
import itch.ac.model.Actividad;
import itch.ac.model.Semestre;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
	List<Inscripcion> findByAlumno(Alumno alumno);

	List<Inscripcion> findByActividad(Actividad actividad);

	List<Inscripcion> findByEstatusSolicitud(String estatusSolicitud);

	List<Inscripcion> findByAlumnoAndEstatusSolicitud(Alumno alumno, String estatusSolicitud);

	List<Inscripcion> findByActividadSemestre(Semestre semestre);

	Inscripcion findByAlumno_IdAndActividad_Semestre_IdAndEstatusSolicitudIn(
			Integer alumnoId, Integer semestreId, List<String> estatusList);
}