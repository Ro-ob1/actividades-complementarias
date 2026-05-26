package itch.ac.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.CriterioBase;
import itch.ac.model.CriterioEval;
import itch.ac.model.Evaluacion;
import itch.ac.model.Inscripcion;
import itch.ac.model.NivelDesempenio;
import itch.ac.service.IActividadService;
import itch.ac.service.IAsistenciaService;
import itch.ac.service.ICriterioBaseService;
import itch.ac.service.ICriterioEvalService;
import itch.ac.service.IEvaluacionService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.INivelDesempenioService;
import itch.ac.service.ISesionService;

@Controller
@RequestMapping("/evaluacion")
public class EvaluacionController {

	@Autowired private IEvaluacionService      evaluacionService;
	@Autowired private IInscripcionService     inscripcionService;
	@Autowired private INivelDesempenioService nivelDesempenioService;
	@Autowired private IActividadService       actividadService;
	@Autowired private IAsistenciaService      asistenciaService;
	@Autowired private ISesionService          sesionService;
	@Autowired private ICriterioEvalService    criterioEvalService;
	@Autowired private ICriterioBaseService    criterioBaseService;

	@GetMapping("/evaluaciones")
	public String listar(
			@RequestParam(required = false) Integer idActividad,
			@RequestParam(required = false) String alumno,
			@RequestParam(required = false) String instructor,
			@RequestParam(required = false) String fechaDesde,
			@RequestParam(required = false) String fechaHasta,
			Model model) {

		List<Evaluacion> evaluaciones = evaluacionService.buscarTodasEvaluaciones();

		if (idActividad != null) {
			evaluaciones = evaluaciones.stream()
					.filter(e -> e.getInscripcion().getActividad().getId().equals(idActividad))
					.collect(Collectors.toList());
		}
		if (alumno != null && !alumno.isBlank()) {
			String q = alumno.toLowerCase();
			evaluaciones = evaluaciones.stream()
					.filter(e -> {
						String nombre = e.getInscripcion().getAlumno().getPersona().getNombre()
								+ " " + e.getInscripcion().getAlumno().getPersona().getApellido();
						return nombre.toLowerCase().contains(q);
					})
					.collect(Collectors.toList());
		}
		if (instructor != null && !instructor.isBlank()) {
			String q = instructor.toLowerCase();
			evaluaciones = evaluaciones.stream()
					.filter(e -> e.getInstructor() != null && (
							e.getInstructor().getPersona().getNombre()
							+ " " + e.getInstructor().getPersona().getApellido())
							.toLowerCase().contains(q))
					.collect(Collectors.toList());
		}
		if (fechaDesde != null && !fechaDesde.isBlank()) {
			LocalDate desde = LocalDate.parse(fechaDesde);
			evaluaciones = evaluaciones.stream()
					.filter(e -> e.getFecha() != null && !e.getFecha().isBefore(desde))
					.collect(Collectors.toList());
		}
		if (fechaHasta != null && !fechaHasta.isBlank()) {
			LocalDate hasta = LocalDate.parse(fechaHasta);
			evaluaciones = evaluaciones.stream()
					.filter(e -> e.getFecha() != null && !e.getFecha().isAfter(hasta))
					.collect(Collectors.toList());
		}

		model.addAttribute("evaluaciones", evaluaciones);
		model.addAttribute("actividades", actividadService.buscarActividadesActivas());
		model.addAttribute("idActividad", idActividad);
		model.addAttribute("alumno", alumno);
		model.addAttribute("instructor", instructor);
		model.addAttribute("fechaDesde", fechaDesde);
		model.addAttribute("fechaHasta", fechaHasta);
		return "evaluacion/listaEvaluaciones";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		// Filtrar inscripciones que ya tienen evaluación registrada
		List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorEstatus("APROBADA")
				.stream()
				.filter(i -> !evaluacionService.existeEvaluacionPorInscripcionId(i.getId()))
				.collect(Collectors.toList());
		model.addAttribute("evaluacion", new Evaluacion());
		model.addAttribute("inscripciones", inscripciones);
		model.addAttribute("criterios", criterioBaseService.buscarTodosCriterios());
		model.addAttribute("niveles", nivelDesempenioService.buscarTodosNiveles());
		return "evaluacion/formularioEvaluacion";
	}

	@PostMapping("/guardar")
	public String guardar(
			Evaluacion evaluacion,
			@RequestParam(required = false) String fechaOriginal,
			@RequestParam(value = "criterioId", required = false) Integer[] criterioIds,
			@RequestParam(value = "criterioValor", required = false) Double[] criterioValores,
			RedirectAttributes attributes) {

		if (evaluacion.getInscripcion() != null && evaluacion.getInscripcion().getId() != null) {
			evaluacion.setInscripcion(inscripcionService.buscarPorId(evaluacion.getInscripcion().getId()));
		}

		Inscripcion inscripcion = evaluacion.getInscripcion();

		if (inscripcion == null || !"APROBADA".equals(inscripcion.getEstatusSolicitud())) {
			attributes.addFlashAttribute("msg", "⚠ Solo se puede evaluar una inscripción aprobada.");
			return evaluacion.getId() != null
					? "redirect:/evaluacion/editar/" + evaluacion.getId()
					: "redirect:/evaluacion/nuevo";
		}

		// Validar 80% asistencia y duplicado (solo para nuevas evaluaciones)
		if (evaluacion.getId() == null) {
			Integer actividadId = inscripcion.getActividad().getId();
			Integer alumnoId    = inscripcion.getAlumno().getId();

			// Usa la misma lógica que el reporte: sesiones no canceladas como denominador
			long totalSesiones = sesionService.contarSesionesNoCanceladas(actividadId);

			if (totalSesiones == 0) {
				attributes.addFlashAttribute("msg",
						"⚠ No se puede evaluar: no hay sesiones registradas para esta actividad.");
				return "redirect:/evaluacion/nuevo";
			}

			long asistencias = asistenciaService.contarAsistenciasNoCanceladas(alumnoId, actividadId);
			double porcentaje = (double) asistencias / totalSesiones * 100;
			if (porcentaje < 80.0) {
				attributes.addFlashAttribute("msg",
						String.format("⚠ El alumno no cumple con el 80%% de asistencia requerido "
								+ "(%.0f%% — %d de %d sesiones).",
								porcentaje, asistencias, totalSesiones));
				return "redirect:/evaluacion/nuevo";
			}

			if (evaluacionService.existeEvaluacionPorInscripcionId(inscripcion.getId())) {
				attributes.addFlashAttribute("msg", "⚠ Ya existe una evaluación para esta inscripción.");
				return "redirect:/evaluacion/nuevo";
			}
		}

		// Instructor derivado automáticamente de la actividad
		evaluacion.setInstructor(inscripcion.getActividad().getInstructor());

		// Nivel derivado automáticamente del valor numérico
		if (evaluacion.getValorNumerico() != null) {
			NivelDesempenio nivel = nivelDesempenioService.buscarPorValor(evaluacion.getValorNumerico());
			evaluacion.setNivel(nivel);
		}

		if (evaluacion.getValorNumerico() == null || evaluacion.getNivel() == null) {
			attributes.addFlashAttribute("msg",
					"⚠ Debes ingresar al menos un criterio de evaluación para calcular el valor.");
			return evaluacion.getId() != null
					? "redirect:/evaluacion/editar/" + evaluacion.getId()
					: "redirect:/evaluacion/nuevo";
		}

		if (evaluacion.getId() == null) {
			evaluacion.setFecha(LocalDate.now());
		} else {
			if (fechaOriginal != null && !fechaOriginal.isEmpty()) {
				evaluacion.setFecha(LocalDate.parse(fechaOriginal));
			} else {
				Evaluacion existente = evaluacionService.buscarPorId(evaluacion.getId());
				if (existente != null) evaluacion.setFecha(existente.getFecha());
			}
		}

		evaluacionService.guardarEvaluacion(evaluacion);

		// Guardar criterioEval
		if (criterioIds != null && criterioValores != null && criterioIds.length == criterioValores.length) {
			// Eliminar criterios anteriores (en edición)
			List<CriterioEval> anteriores = criterioEvalService.buscarCriteriosPorEvaluacionId(evaluacion.getId());
			for (CriterioEval ce : anteriores) {
				criterioEvalService.eliminarPorId(ce.getId());
			}
			// Guardar nuevos
			for (int i = 0; i < criterioIds.length; i++) {
				if (criterioValores[i] != null) {
					CriterioBase criterio = criterioBaseService.buscarPorId(criterioIds[i]);
					NivelDesempenio nivelCriterio = nivelDesempenioService.buscarPorValor(criterioValores[i]);
					CriterioEval ce = new CriterioEval();
					ce.setEvaluacion(evaluacion);
					ce.setCriterio(criterio);
					ce.setValor(criterioValores[i]);
					ce.setNivel(nivelCriterio);
					criterioEvalService.guardarCriterioEval(ce);
				}
			}
		}

		attributes.addFlashAttribute("msg", "Evaluación guardada correctamente.");
		return "redirect:/evaluacion/evaluaciones";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		model.addAttribute("evaluacion", evaluacionService.buscarPorId(id));
		model.addAttribute("criteriosEval", criterioEvalService.buscarCriteriosPorEvaluacionId(id));
		return "evaluacion/detalleEvaluacion";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("evaluacion", evaluacionService.buscarPorId(id));
		model.addAttribute("criterios", criterioBaseService.buscarTodosCriterios());
		model.addAttribute("criteriosEval", criterioEvalService.buscarCriteriosPorEvaluacionId(id));
		model.addAttribute("niveles", nivelDesempenioService.buscarTodosNiveles());
		return "evaluacion/formularioEvaluacion";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		// Eliminar criteriosEval asociados primero
		List<CriterioEval> criterios = criterioEvalService.buscarCriteriosPorEvaluacionId(id);
		for (CriterioEval ce : criterios) {
			criterioEvalService.eliminarPorId(ce.getId());
		}
		Evaluacion evaluacion = evaluacionService.eliminarPorId(id);
		if (evaluacion == null) {
			attributes.addFlashAttribute("msg", "⚠ No se pudo eliminar la evaluación.");
		} else {
			attributes.addFlashAttribute("msg", "Evaluación eliminada correctamente.");
		}
		return "redirect:/evaluacion/evaluaciones";
	}
}
