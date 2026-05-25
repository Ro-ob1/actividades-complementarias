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

import itch.ac.model.Actividad;
import itch.ac.model.Asistencia;
import itch.ac.model.Sesion;
import itch.ac.service.IActividadService;
import itch.ac.service.IAsistenciaService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.ISesionService;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {

	@Autowired private IAsistenciaService asistenciaService;
	@Autowired private IActividadService  actividadService;
	@Autowired private ISesionService     sesionService;
	@Autowired private IInscripcionService inscripcionService;

	@GetMapping("/asistencias")
	public String listar(
			@RequestParam(required = false) Integer idActividad,
			@RequestParam(required = false) Integer idSesion,
			@RequestParam(required = false) String filtroAsistio,
			@RequestParam(required = false) String fechaDesde,
			@RequestParam(required = false) String fechaHasta,
			Model model) {

		if (idActividad == null) {
			List<ActividadResumenAsistDto> resumenes = actividadService.buscarActividadesActivas()
					.stream()
					.map(act -> {
						List<Sesion> sesiones = sesionService.buscarSesionesPorActividad(act);
						int realizadas = (int) sesiones.stream()
								.filter(s -> "REALIZADA".equals(s.getEstatus())).count();
						int inscritos = (int) inscripcionService.buscarInscripcionesPorActividad(act)
								.stream().filter(i -> "APROBADA".equals(i.getEstatusSolicitud())).count();
						return new ActividadResumenAsistDto(act, realizadas, inscritos);
					})
					.filter(r -> r.getSesionesRealizadas() > 0)
					.collect(Collectors.toList());

			model.addAttribute("resumenes", resumenes);
		} else {
			Actividad actividad = actividadService.buscarPorId(idActividad);

			List<Sesion> sesiones = sesionService.buscarSesionesPorActividad(actividad)
					.stream()
					.filter(s -> "REALIZADA".equals(s.getEstatus()))
					.collect(Collectors.toList());

			List<Asistencia> asistencias = asistenciaService.buscarTodasAsistencias()
					.stream()
					.filter(a -> a.getSesion().getHorario().getActividad().getId().equals(idActividad))
					.filter(a -> !"CANCELADA".equals(a.getSesion().getEstatus()))
					.collect(Collectors.toList());

			if (idSesion != null) {
				asistencias = asistencias.stream()
						.filter(a -> a.getSesion().getId().equals(idSesion))
						.collect(Collectors.toList());
			}
			if (fechaDesde != null && !fechaDesde.isEmpty()) {
				LocalDate desde = LocalDate.parse(fechaDesde);
				asistencias = asistencias.stream()
						.filter(a -> !a.getSesion().getFecha().isBefore(desde))
						.collect(Collectors.toList());
			}
			if (fechaHasta != null && !fechaHasta.isEmpty()) {
				LocalDate hasta = LocalDate.parse(fechaHasta);
				asistencias = asistencias.stream()
						.filter(a -> !a.getSesion().getFecha().isAfter(hasta))
						.collect(Collectors.toList());
			}
			if ("SI".equals(filtroAsistio)) {
				asistencias = asistencias.stream()
						.filter(a -> Boolean.TRUE.equals(a.getAsistio()))
						.collect(Collectors.toList());
			} else if ("NO".equals(filtroAsistio)) {
				asistencias = asistencias.stream()
						.filter(a -> !Boolean.TRUE.equals(a.getAsistio()))
						.collect(Collectors.toList());
			}

			model.addAttribute("actividad", actividad);
			model.addAttribute("sesiones", sesiones);
			model.addAttribute("asistencias", asistencias);
			model.addAttribute("idSesion", idSesion);
			model.addAttribute("filtroAsistio", filtroAsistio);
			model.addAttribute("fechaDesde", fechaDesde);
			model.addAttribute("fechaHasta", fechaHasta);
		}

		model.addAttribute("idActividad", idActividad);
		return "asistencia/listaAsistencias";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		model.addAttribute("asistencia", asistenciaService.buscarPorId(id));
		return "asistencia/detalleAsistencia";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("asistencia", asistenciaService.buscarPorId(id));
		return "asistencia/formularioAsistencia";
	}

	@PostMapping("/guardar")
	public String guardar(
			@RequestParam Integer id,
			@RequestParam(required = false) Boolean asistio,
			@RequestParam(required = false) String observaciones,
			RedirectAttributes attributes) {

		Asistencia asistencia = asistenciaService.buscarPorId(id);
		if (asistencia != null) {
			Integer idActividad = asistencia.getSesion().getHorario().getActividad().getId();
			asistencia.setAsistio(Boolean.TRUE.equals(asistio));
			asistencia.setObservaciones(
					observaciones != null && !observaciones.isBlank() ? observaciones.trim() : null);
			asistenciaService.guardarAsistencia(asistencia);
			attributes.addFlashAttribute("msg", "Registro de asistencia actualizado.");
			return "redirect:/asistencia/asistencias?idActividad=" + idActividad;
		}
		return "redirect:/asistencia/asistencias";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Asistencia asistencia = asistenciaService.buscarPorId(id);
		if (asistencia != null) {
			Integer idActividad = asistencia.getSesion().getHorario().getActividad().getId();
			asistenciaService.eliminarPorId(id);
			attributes.addFlashAttribute("msg", "Registro eliminado.");
			return "redirect:/asistencia/asistencias?idActividad=" + idActividad;
		}
		attributes.addFlashAttribute("msg", "⚠ No se pudo eliminar el registro.");
		return "redirect:/asistencia/asistencias";
	}

	public static class ActividadResumenAsistDto {
		private final Actividad actividad;
		private final int sesionesRealizadas;
		private final int inscritos;

		public ActividadResumenAsistDto(Actividad actividad, int sesionesRealizadas, int inscritos) {
			this.actividad = actividad;
			this.sesionesRealizadas = sesionesRealizadas;
			this.inscritos = inscritos;
		}

		public Actividad getActividad()       { return actividad; }
		public int getSesionesRealizadas()    { return sesionesRealizadas; }
		public int getInscritos()             { return inscritos; }
	}
}
