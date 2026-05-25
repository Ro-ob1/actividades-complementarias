package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Actividad;
import itch.ac.model.Semestre;
import itch.ac.repository.ActividadRepository;
import itch.ac.service.IActividadService;

@Primary
@Service
public class ActividadServiceJPA implements IActividadService {

	@Autowired
	private ActividadRepository actividadRepository;

	@Override
	public List<Actividad> buscarTodasActividades() {
		return actividadRepository.findAll();
	}

	@Override
	public List<Actividad> buscarActividadesActivas() {
		return actividadRepository.findAll().stream()
			.filter(a -> Integer.valueOf(1).equals(a.getActivo()))
			.collect(java.util.stream.Collectors.toList());
	}

	@Override
	public List<Actividad> buscarActividadesPorSemestre(Semestre semestre) {
		return actividadRepository.findBySemestre(semestre);
	}

	@Override
	public List<Actividad> buscarActividadesActivasPorSemestre(Semestre semestre) {
		return actividadRepository.findByActivoAndSemestre(1, semestre);
	}

	@Override
	public List<Actividad> buscarPorNombre(String nombre) {
		return actividadRepository.findByNombreContainingIgnoreCase(nombre);
	}

	@Override
	public boolean existePorNombre(String nombre, Integer idExcluir, Integer idSemestre) {
		if (nombre == null || nombre.trim().isEmpty()) return false;
		return actividadRepository.findAll().stream()
			.filter(a -> idExcluir == null || !a.getId().equals(idExcluir))
			.filter(a -> idSemestre == null
				|| (a.getSemestre() != null && a.getSemestre().getId().equals(idSemestre)))
			.anyMatch(a -> a.getNombre() != null && a.getNombre().equalsIgnoreCase(nombre.trim()));
	}

	@Override
	public void desactivarActividadesPorSemestre(Semestre semestre) {
		actividadRepository.findBySemestre(semestre).stream()
			.filter(a -> Integer.valueOf(1).equals(a.getActivo()))
			.forEach(a -> {
				a.setActivo(0);
				actividadRepository.save(a);
			});
	}

	@Override
	public void guardarActividad(Actividad actividad) {
		actividadRepository.save(actividad);
	}

	@Override
	public Actividad buscarPorId(Integer id) {
		Optional<Actividad> optional = actividadRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Actividad eliminarPorId(Integer id) {
		Optional<Actividad> optional = actividadRepository.findById(id);
		if (optional.isPresent()) {
			Actividad actividad = optional.get();
			actividad.setActivo(0);
			actividadRepository.save(actividad);
			return actividad;
		}
		return null;
	}
}