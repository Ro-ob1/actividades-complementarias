package itch.ac.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.IDisciplinaService;
import itch.ac.service.IHorarioService;
import itch.ac.service.IInstructorService;
import itch.ac.service.ISemestreService;
import itch.ac.service.IUsuarioService;

@Controller
@RequestMapping("/actividad")
public class ActividadController {

	@Autowired
	private IActividadService actividadService;

	@Autowired
	private IDisciplinaService disciplinaService;

	@Autowired
	private ISemestreService semestreService;

	@Autowired
	private IInstructorService instructorService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IHorarioService horarioService;

	@Value("${upload.path}")
	private String uploadPath;

	@GetMapping("/actividades")
	public String listar(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Integer idSemestre, Model model, Authentication auth) {

		boolean isAdmin = auth.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		boolean isInstructor = auth.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

		Semestre semestre = null;
		if (idSemestre != null) {
			semestre = semestreService.buscarPorId(idSemestre);
		}

		List<Actividad> actividades;
		if (nombre != null && !nombre.trim().isEmpty()) {
			actividades = actividadService.buscarPorNombre(nombre);
			if (!isAdmin) {
				actividades = actividades.stream()
					.filter(a -> Integer.valueOf(1).equals(a.getActivo()))
					.collect(java.util.stream.Collectors.toList());
			}
		} else if (semestre != null) {
			actividades = isAdmin
				? actividadService.buscarActividadesPorSemestre(semestre)
				: actividadService.buscarActividadesActivasPorSemestre(semestre);
		} else {
			actividades = isAdmin
				? actividadService.buscarTodasActividades()
				: actividadService.buscarActividadesActivas();
		}

		if (isInstructor) {
			var usuario = usuarioService.buscarPorUsername(auth.getName());
			Integer personaId = usuario.getPersona().getId();
			actividades = actividades.stream()
				.filter(a -> a.getInstructor() != null
						  && a.getInstructor().getPersona() != null
						  && a.getInstructor().getPersona().getId().equals(personaId))
				.collect(java.util.stream.Collectors.toList());
			model.addAttribute("soloMisActividades", true);
		}

		model.addAttribute("actividades", actividades);
		model.addAttribute("semestres", semestreService.buscarTodosSemestres());
		model.addAttribute("nombre", nombre);
		model.addAttribute("idSemestre", idSemestre);
		return "actividad/listaActividades";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		Actividad actividad = new Actividad();
		actividad.setSemestre(semestreService.buscarSemestreActivo());
		model.addAttribute("actividad", actividad);
		model.addAttribute("disciplinas", disciplinaService.buscarTodasDisciplinas());
		model.addAttribute("instructores", instructorService.buscarInstructoresActivos());
		return "actividad/formularioActividad";
	}

	@PostMapping("/guardar")
	public String guardar(Actividad actividad,
			@RequestParam("imagenArchivo") MultipartFile archivo,
			@RequestParam(required = false) String imagenActual,
			RedirectAttributes attributes) {

		// 1. Validar duplicado de nombre dentro del mismo semestre
		if (actividad.getId() != null) {
			// Edición: obtener semestre desde BD y solo validar si el nombre cambió
			Actividad existente = actividadService.buscarPorId(actividad.getId());
			boolean nombreCambio = existente == null
				|| !existente.getNombre().equalsIgnoreCase(actividad.getNombre() != null ? actividad.getNombre().trim() : "");
			if (nombreCambio) {
				Integer idSemestre = (existente != null && existente.getSemestre() != null)
					? existente.getSemestre().getId() : null;
				if (actividadService.existePorNombre(actividad.getNombre(), actividad.getId(), idSemestre)) {
					attributes.addFlashAttribute("msg", "⚠ Ya existe una actividad con ese nombre en este semestre.");
					return "redirect:/actividad/actividades";
				}
			}
		} else {
			// Nueva actividad: validar con el semestre que viene del formulario
			Integer idSemestre = (actividad.getSemestre() != null) ? actividad.getSemestre().getId() : null;
			if (actividadService.existePorNombre(actividad.getNombre(), null, idSemestre)) {
				attributes.addFlashAttribute("msg", "⚠ Ya existe una actividad con ese nombre en este semestre.");
				return "redirect:/actividad/actividades";
			}
		}

		// 2. Capitalizar nombre y descripción
		if (actividad.getNombre() != null) {
			actividad.setNombre(capitalizarPalabras(actividad.getNombre()));
		}
		if (actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()) {
			actividad.setDescripcion(capitalizarPalabras(actividad.getDescripcion()));
		}

		// 3. Subir imagen
		if (!archivo.isEmpty()) {
			try {
				String nombreArchivo = archivo.getOriginalFilename();
				Path ruta = Paths.get(uploadPath + "/actividad/" + nombreArchivo);
				Files.createDirectories(ruta.getParent());
				Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
				actividad.setImagenFlyer(nombreArchivo);
			} catch (Exception e) {
				actividad.setImagenFlyer("no-image.jpg");
			}
		} else {
			actividad.setImagenFlyer(
					imagenActual != null && !imagenActual.isEmpty() ? imagenActual : "no-image.jpg");
		}

		// 4. Activo automático al crear
		if (actividad.getId() == null) {
			actividad.setActivo(1);
		}

		actividadService.guardarActividad(actividad);
		attributes.addFlashAttribute("msg", "Actividad guardada correctamente.");
		return "redirect:/actividad/actividades";
	}

	private String capitalizarPalabras(String texto) {
		if (texto == null || texto.isEmpty()) return texto;
		String[] palabras = texto.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String palabra : palabras) {
			if (!palabra.isEmpty()) {
				sb.append(Character.toUpperCase(palabra.charAt(0)))
				  .append(palabra.substring(1).toLowerCase()).append(" ");
			}
		}
		return sb.toString().trim();
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Actividad actividad = actividadService.buscarPorId(id);
		if (actividad == null) {
			return "redirect:/actividad/actividades";
		}
		model.addAttribute("actividad", actividad);
		model.addAttribute("horarios", horarioService.buscarHorariosPorActividad(actividad));
		return "actividad/detalleActividad";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Actividad actividad = actividadService.buscarPorId(id);
		if (actividad == null) {
			return "redirect:/actividad/actividades";
		}
		model.addAttribute("actividad", actividad);
		model.addAttribute("disciplinas", disciplinaService.buscarTodasDisciplinas());
		model.addAttribute("instructores", instructorService.buscarInstructoresActivos());
		return "actividad/formularioActividad";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Actividad actividad = actividadService.eliminarPorId(id);
		if (actividad == null) {
			attributes.addFlashAttribute("msg", "⚠ No se encontró la actividad.");
		} else {
			attributes.addFlashAttribute("msg", "Actividad desactivada correctamente.");
		}
		return "redirect:/actividad/actividades";
	}
}
