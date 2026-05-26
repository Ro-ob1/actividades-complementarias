package itch.ac.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

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
import itch.ac.model.Alumno;
import itch.ac.model.Asistencia;
import itch.ac.model.Inscripcion;
import itch.ac.model.Sesion;
import itch.ac.service.IActividadService;
import itch.ac.service.IAsistenciaService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.ISesionService;

@Controller
@RequestMapping("/sesion")
public class SesionController {

	@Autowired private ISesionService sesionService;
	@Autowired private IAsistenciaService asistenciaService;
	@Autowired private IInscripcionService inscripcionService;
	@Autowired private IActividadService actividadService;

	@GetMapping("/sesiones")
	public String listar(
			@RequestParam(required = false) Integer idActividad,
			@RequestParam(required = false) String fechaDesde,
			@RequestParam(required = false) String fechaHasta,
			Model model) {

		if (idActividad != null) {
			Actividad actividad = actividadService.buscarPorId(idActividad);
			List<Sesion> sesiones = sesionService.buscarSesionesPorActividad(actividad);

			LocalDate desde = (fechaDesde != null && !fechaDesde.isEmpty()) ? LocalDate.parse(fechaDesde) : null;
			LocalDate hasta = (fechaHasta != null && !fechaHasta.isEmpty()) ? LocalDate.parse(fechaHasta) : null;

			if (desde != null) {
				final LocalDate d = desde;
				sesiones = sesiones.stream().filter(s -> !s.getFecha().isBefore(d)).collect(Collectors.toList());
			}
			if (hasta != null) {
				final LocalDate h = hasta;
				sesiones = sesiones.stream().filter(s -> !s.getFecha().isAfter(h)).collect(Collectors.toList());
			}

			model.addAttribute("sesiones", sesiones);
			model.addAttribute("actividadSeleccionada", actividad);
			model.addAttribute("fechaDesde", fechaDesde);
			model.addAttribute("fechaHasta", fechaHasta);
		} else {
			List<ActividadResumenDto> resumenes = actividadService.buscarActividadesActivas()
					.stream()
					.map(a -> {
						List<Sesion> s = sesionService.buscarSesionesPorActividad(a);
						long realizadas  = s.stream().filter(x -> "REALIZADA".equals(x.getEstatus())).count();
						long canceladas  = s.stream().filter(x -> "CANCELADA".equals(x.getEstatus())).count();
						long programadas = s.stream().filter(x -> "PROGRAMADA".equals(x.getEstatus())).count();
						return new ActividadResumenDto(a, s.size(), (int) realizadas, (int) programadas, (int) canceladas);
					})
					.filter(r -> r.getTotalSesiones() > 0)
					.collect(Collectors.toList());
			model.addAttribute("resumenes", resumenes);
		}

		model.addAttribute("idActividad", idActividad);
		return "sesion/listaSesiones";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Sesion sesion = sesionService.buscarPorId(id);
		List<Asistencia> asistencias = asistenciaService.buscarAsistenciasPorSesion(sesion);
		model.addAttribute("sesion", sesion);
		model.addAttribute("asistencias", asistencias);
		return "sesion/detalleSesion";
	}

	@GetMapping("/asistencia/{id}")
	public String registrarAsistencia(@PathVariable Integer id, Model model) {
		Sesion sesion = sesionService.buscarPorId(id);
		Actividad actividad = sesion.getHorario().getActividad();

		List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorActividad(actividad)
				.stream()
				.filter(i -> "APROBADA".equalsIgnoreCase(i.getEstatusSolicitud()))
				.collect(Collectors.toList());

		Map<Integer, Asistencia> asistenciaMap = asistenciaService.buscarAsistenciasPorSesion(sesion)
				.stream()
				.collect(Collectors.toMap(a -> a.getAlumno().getId(), a -> a));

		model.addAttribute("sesion", sesion);
		model.addAttribute("inscripciones", inscripciones);
		model.addAttribute("asistenciaMap", asistenciaMap);
		return "sesion/registrarAsistencia";
	}

	@PostMapping("/guardarAsistencia")
	public String guardarAsistencia(
			@RequestParam Integer idSesion,
			@RequestParam(value = "asistioIds", required = false) List<Integer> asistioIds,
			@RequestParam(required = false) String redirectTo,
			HttpServletRequest request,
			RedirectAttributes attributes) {

		Sesion sesion = sesionService.buscarPorId(idSesion);
		Actividad actividad = sesion.getHorario().getActividad();

		List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorActividad(actividad)
				.stream()
				.filter(i -> "APROBADA".equalsIgnoreCase(i.getEstatusSolicitud()))
				.collect(Collectors.toList());

		for (Inscripcion insc : inscripciones) {
			Alumno alumno = insc.getAlumno();
			Asistencia asistencia = asistenciaService.buscarPorAlumnoYSesion(alumno, sesion);
			if (asistencia == null) asistencia = new Asistencia();
			asistencia.setAlumno(alumno);
			asistencia.setSesion(sesion);
			boolean asistio = asistioIds != null && asistioIds.contains(alumno.getId());
			asistencia.setAsistio(asistio);
			String obs = request.getParameter("obs_" + alumno.getId());
			asistencia.setObservaciones(obs != null && obs.isBlank() ? null : obs);
			asistenciaService.guardarAsistencia(asistencia);
		}

		sesion.setEstatus("REALIZADA");
		sesionService.guardarSesion(sesion);

		attributes.addFlashAttribute("msg", "Asistencia registrada correctamente.");
		if ("panel".equals(redirectTo)) return "redirect:/instructor/inicio";
		return "redirect:/sesion/sesiones?idActividad=" + actividad.getId();
	}

	@PostMapping("/marcarTodosAsistieron")
	public String marcarTodosAsistieron(
			@RequestParam Integer idSesion,
			@RequestParam Integer idActividad,
			@RequestParam(required = false) String redirectTo,
			RedirectAttributes attributes) {

		Sesion sesion = sesionService.buscarPorId(idSesion);
		if (sesion == null || "CANCELADA".equals(sesion.getEstatus())) {
			attributes.addFlashAttribute("msg", "⚠ La sesión no existe o está cancelada.");
			return "redirect:/sesion/sesiones?idActividad=" + idActividad;
		}

		Actividad actividad = actividadService.buscarPorId(idActividad);
		List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorActividad(actividad)
				.stream()
				.filter(i -> "APROBADA".equalsIgnoreCase(i.getEstatusSolicitud()))
				.collect(Collectors.toList());

		for (Inscripcion insc : inscripciones) {
			Alumno alumno = insc.getAlumno();
			Asistencia asistencia = asistenciaService.buscarPorAlumnoYSesion(alumno, sesion);
			if (asistencia == null) asistencia = new Asistencia();
			asistencia.setAlumno(alumno);
			asistencia.setSesion(sesion);
			asistencia.setAsistio(true);
			asistenciaService.guardarAsistencia(asistencia);
		}

		sesion.setEstatus("REALIZADA");
		sesionService.guardarSesion(sesion);

		attributes.addFlashAttribute("msg",
				"Sesión #" + sesion.getNumeroSesion() + " marcada como realizada. "
				+ inscripciones.size() + " alumno(s) registrado(s) con asistencia.");
		if ("panel".equals(redirectTo)) return "redirect:/instructor/inicio";
		return "redirect:/sesion/sesiones?idActividad=" + idActividad;
	}

	@PostMapping("/bulkAccion")
	public String bulkAccion(
			@RequestParam(required = false) List<Integer> ids,
			@RequestParam String accion,
			@RequestParam Integer idActividad,
			RedirectAttributes attributes) {

		if (ids == null || ids.isEmpty()) {
			attributes.addFlashAttribute("msg", "⚠ No seleccionaste ninguna sesión.");
			return "redirect:/sesion/sesiones?idActividad=" + idActividad;
		}

		if ("REALIZAR".equals(accion)) {
			int realizadas = 0;
			for (Integer id : ids) {
				Sesion sesion = sesionService.buscarPorId(id);
				if (sesion == null || !"PROGRAMADA".equals(sesion.getEstatus())) continue;
				Actividad actividad = sesion.getHorario().getActividad();
				List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorActividad(actividad)
						.stream()
						.filter(i -> "APROBADA".equalsIgnoreCase(i.getEstatusSolicitud()))
						.collect(Collectors.toList());
				for (Inscripcion insc : inscripciones) {
					Alumno alumno = insc.getAlumno();
					Asistencia asistencia = asistenciaService.buscarPorAlumnoYSesion(alumno, sesion);
					if (asistencia == null) asistencia = new Asistencia();
					asistencia.setAlumno(alumno);
					asistencia.setSesion(sesion);
					asistencia.setAsistio(true);
					asistenciaService.guardarAsistencia(asistencia);
				}
				sesion.setEstatus("REALIZADA");
				sesionService.guardarSesion(sesion);
				realizadas++;
			}
			attributes.addFlashAttribute("msg", realizadas + " sesión(es) marcadas como realizadas con asistencia completa.");
			return "redirect:/sesion/sesiones?idActividad=" + idActividad;
		}

		String nuevoEstatus = "CANCELAR".equals(accion) ? "CANCELADA" : "PROGRAMADA";
		for (Integer id : ids) {
			Sesion sesion = sesionService.buscarPorId(id);
			if (sesion != null) {
				sesion.setEstatus(nuevoEstatus);
				sesionService.guardarSesion(sesion);
			}
		}

		String msg = "CANCELAR".equals(accion)
				? ids.size() + " sesión(es) cancelada(s)."
				: ids.size() + " sesión(es) reactivada(s).";
		attributes.addFlashAttribute("msg", msg);
		return "redirect:/sesion/sesiones?idActividad=" + idActividad;
	}

	@GetMapping("/reporte/{idActividad}")
	public String reporte(@PathVariable Integer idActividad, Model model) {
		Actividad actividad = actividadService.buscarPorId(idActividad);
		List<Sesion> sesiones = sesionService.buscarSesionesPorActividad(actividad);
		int totalSesiones = (int) sesiones.stream()
				.filter(s -> !"CANCELADA".equals(s.getEstatus()))
				.count();

		List<Inscripcion> inscripciones = inscripcionService.buscarInscripcionesPorActividad(actividad)
				.stream()
				.filter(i -> "APROBADA".equalsIgnoreCase(i.getEstatusSolicitud()))
				.collect(Collectors.toList());

		List<ReporteAlumnoDto> reporte = new ArrayList<>();
		for (Inscripcion insc : inscripciones) {
			Alumno alumno = insc.getAlumno();
			long asistidas = asistenciaService.contarAsistenciasPorAlumnoYActividad(alumno, actividad);
			double porcentaje = totalSesiones > 0 ? (asistidas * 100.0 / totalSesiones) : 0;
			boolean acreditado = porcentaje >= 80.0;
			reporte.add(new ReporteAlumnoDto(alumno, totalSesiones, (int) asistidas, porcentaje, acreditado));
		}

		model.addAttribute("actividad", actividad);
		model.addAttribute("reporte", reporte);
		model.addAttribute("totalSesiones", totalSesiones);
		return "sesion/reporteAsistencia";
	}

	public static class ActividadResumenDto {
		private final Actividad actividad;
		private final int totalSesiones;
		private final int sesionesRealizadas;
		private final int sesionesProgramadas;
		private final int sesionesCanceladas;

		public ActividadResumenDto(Actividad actividad, int total, int realizadas, int programadas, int canceladas) {
			this.actividad = actividad;
			this.totalSesiones = total;
			this.sesionesRealizadas = realizadas;
			this.sesionesProgramadas = programadas;
			this.sesionesCanceladas = canceladas;
		}

		public Actividad getActividad() { return actividad; }
		public int getTotalSesiones() { return totalSesiones; }
		public int getSesionesRealizadas() { return sesionesRealizadas; }
		public int getSesionesProgramadas() { return sesionesProgramadas; }
		public int getSesionesCanceladas() { return sesionesCanceladas; }
	}

	public static class ReporteAlumnoDto {
		private final Alumno alumno;
		private final int totalSesiones;
		private final int sesionesAsistidas;
		private final double porcentaje;
		private final boolean acreditado;

		public ReporteAlumnoDto(Alumno alumno, int totalSesiones, int sesionesAsistidas,
				double porcentaje, boolean acreditado) {
			this.alumno = alumno;
			this.totalSesiones = totalSesiones;
			this.sesionesAsistidas = sesionesAsistidas;
			this.porcentaje = porcentaje;
			this.acreditado = acreditado;
		}

		public Alumno getAlumno() { return alumno; }
		public int getTotalSesiones() { return totalSesiones; }
		public int getSesionesAsistidas() { return sesionesAsistidas; }
		public double getPorcentaje() { return porcentaje; }
		public boolean isAcreditado() { return acreditado; }
	}
}
