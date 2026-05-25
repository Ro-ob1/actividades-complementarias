package itch.ac.controller;

import java.time.LocalDate;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Actividad;
import itch.ac.model.Encargado;
import itch.ac.model.Inscripcion;
import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.IAlumnoService;
import itch.ac.service.IEncargadoService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.ISemestreService;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {

	private static final long MAX_PDF_BYTES = 10 * 1024 * 1024; // 10 MB

	@Autowired private IInscripcionService inscripcionService;
	@Autowired private IAlumnoService alumnoService;
	@Autowired private IActividadService actividadService;
	@Autowired private IEncargadoService encargadoService;
	@Autowired private ISemestreService semestreService;

	@Value("${app.upload.dir:/uploads}")
	private String uploadDir;

	@GetMapping("/inscripciones")
	public String listar(
			@RequestParam(required = false) String estatus,
			@RequestParam(required = false) Integer idSemestre,
			Model model) {

		List<Inscripcion> inscripciones;
		Semestre semestre = null;

		if (idSemestre != null) {
			semestre = semestreService.buscarPorId(idSemestre);
		}

		if (estatus != null && !estatus.trim().isEmpty() && semestre != null) {
			inscripciones = inscripcionService.buscarInscripcionesPorSemestre(semestre).stream()
					.filter(i -> i.getEstatusSolicitud().equals(estatus)).toList();
		} else if (semestre != null) {
			inscripciones = inscripcionService.buscarInscripcionesPorSemestre(semestre);
		} else if (estatus != null && !estatus.trim().isEmpty()) {
			inscripciones = inscripcionService.buscarInscripcionesPorEstatus(estatus);
		} else {
			inscripciones = inscripcionService.buscarTodasInscripciones();
		}

		model.addAttribute("inscripciones", inscripciones);
		model.addAttribute("semestres", semestreService.buscarTodosSemestres());
		model.addAttribute("estatus", estatus);
		model.addAttribute("idSemestre", idSemestre);
		return "inscripcion/listaInscripciones";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		List<Encargado> encargados = encargadoService.buscarEncargadosActivos();
		Semestre semestreActivo = semestreService.buscarSemestreActivo();
		model.addAttribute("inscripcion", new Inscripcion());
		model.addAttribute("alumnos", alumnoService.buscarAlumnosActivos());
		model.addAttribute("actividades", semestreActivo != null
				? actividadService.buscarActividadesActivasPorSemestre(semestreActivo)
				: actividadService.buscarActividadesActivas());
		model.addAttribute("encargadoAuto", encargados.isEmpty() ? null : encargados.get(0));
		return "inscripcion/formularioInscripcion";
	}

	@PostMapping("/guardar")
	public String guardar(
			Inscripcion inscripcion,
			@RequestParam("archivoHorarioPdf") MultipartFile archivo,
			@RequestParam(required = false) String archivoActual,
			RedirectAttributes attributes) {

		if (inscripcion.getActividad() != null && inscripcion.getActividad().getId() != null) {
			inscripcion.setActividad(actividadService.buscarPorId(inscripcion.getActividad().getId()));
		}
		if (inscripcion.getAlumno() != null && inscripcion.getAlumno().getId() != null) {
			inscripcion.setAlumno(alumnoService.buscarPorId(inscripcion.getAlumno().getId()));
		}
		if (inscripcion.getEncargado() != null && inscripcion.getEncargado().getId() != null) {
			inscripcion.setEncargado(encargadoService.buscarPorId(inscripcion.getEncargado().getId()));
		} else {
			// Auto-asignar primer encargado activo si no se especificó
			List<Encargado> activos = encargadoService.buscarEncargadosActivos();
			inscripcion.setEncargado(activos.isEmpty() ? null : activos.get(0));
		}

		if (inscripcion.getId() == null) {
			// Nueva inscripción: fecha de hoy y estatus inicial
			inscripcion.setFechaSolicitud(LocalDate.now());
			inscripcion.setEstatusSolicitud("PENDIENTE");

			Semestre semestre = inscripcion.getActividad() != null
					? inscripcion.getActividad().getSemestre() : null;
			if (inscripcionService.alumnoTieneInscripcionActiva(inscripcion.getAlumno(), semestre)) {
				attributes.addFlashAttribute("msg",
						"⚠ El alumno ya tiene una inscripción activa en este semestre.");
				return "redirect:/inscripcion/nuevo";
			}
		}

		if (!archivo.isEmpty()) {
			if (archivo.getSize() > MAX_PDF_BYTES) {
				attributes.addFlashAttribute("msg",
						"⚠ El archivo PDF supera el límite de 10 MB. Por favor usa un archivo más pequeño.");
				String redir = inscripcion.getId() != null
						? "redirect:/inscripcion/editar/" + inscripcion.getId()
						: "redirect:/inscripcion/nuevo";
				return redir;
			}
			try {
				String nombreArchivo = archivo.getOriginalFilename();
				Path ruta = Paths.get(uploadDir + "/inscripcion/" + nombreArchivo);
				Files.createDirectories(ruta.getParent());
				Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
				inscripcion.setArchivoHorario(nombreArchivo);
			} catch (Exception e) {
				inscripcion.setArchivoHorario(archivoActual);
			}
		} else {
			if (archivoActual != null && !archivoActual.isEmpty()) {
				inscripcion.setArchivoHorario(archivoActual);
			}
		}

		inscripcionService.guardarInscripcion(inscripcion);
		attributes.addFlashAttribute("msg", "Inscripción guardada correctamente.");
		return "redirect:/inscripcion/inscripciones";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		model.addAttribute("inscripcion", inscripcionService.buscarPorId(id));
		return "inscripcion/detalleInscripcion";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Inscripcion inscripcion = inscripcionService.buscarPorId(id);
		Semestre semestreActivo = semestreService.buscarSemestreActivo();
		model.addAttribute("inscripcion", inscripcion);
		model.addAttribute("alumnos", alumnoService.buscarAlumnosActivos());
		model.addAttribute("actividades", semestreActivo != null
				? actividadService.buscarActividadesActivasPorSemestre(semestreActivo)
				: actividadService.buscarActividadesActivas());
		model.addAttribute("encargados", encargadoService.buscarEncargadosActivos());
		return "inscripcion/formularioInscripcion";
	}

	@GetMapping("/aprobar/{id}")
	public String aprobar(@PathVariable Integer id, RedirectAttributes attributes) {
		Inscripcion inscripcion = inscripcionService.buscarPorId(id);
		if (inscripcion != null) {
			Actividad actividad = inscripcion.getActividad();
			if (!inscripcionService.actividadTieneCupoDisponible(actividad)) {
				attributes.addFlashAttribute("msg",
						"⚠ No se puede aprobar: la actividad ha alcanzado su cupo máximo.");
			} else {
				inscripcion.setEstatusSolicitud("APROBADA");
				inscripcionService.guardarInscripcion(inscripcion);
				attributes.addFlashAttribute("msg", "Inscripción aprobada correctamente.");
			}
		}
		return "redirect:/inscripcion/inscripciones";
	}

	@GetMapping("/rechazar/{id}")
	public String rechazar(@PathVariable Integer id,
			@RequestParam String motivoRechazo,
			RedirectAttributes attributes) {
		Inscripcion inscripcion = inscripcionService.buscarPorId(id);
		if (inscripcion != null) {
			inscripcion.setEstatusSolicitud("RECHAZADA");
			inscripcion.setMotivoRechazo(motivoRechazo);
			inscripcionService.guardarInscripcion(inscripcion);
			attributes.addFlashAttribute("msg", "Inscripción rechazada.");
		}
		return "redirect:/inscripcion/inscripciones";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Inscripcion inscripcion = inscripcionService.eliminarPorId(id);
		if (inscripcion == null) {
			attributes.addFlashAttribute("msg", "⚠ No se pudo eliminar la inscripción.");
		} else {
			attributes.addFlashAttribute("msg", "Inscripción eliminada correctamente.");
		}
		return "redirect:/inscripcion/inscripciones";
	}
}
