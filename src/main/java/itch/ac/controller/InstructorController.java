package itch.ac.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Inscripcion;
import itch.ac.model.Instructor;
import itch.ac.model.Persona;
import itch.ac.model.Sesion;
import itch.ac.model.Actividad;
import itch.ac.service.IActividadService;
import itch.ac.service.IAsistenciaService;
import itch.ac.service.IDisciplinaService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.IInstructorService;
import itch.ac.service.IPersonaService;
import itch.ac.service.ISemestreService;
import itch.ac.service.ISesionService;
import itch.ac.service.IUsuarioService;

@Controller
@RequestMapping("/instructor")
public class InstructorController {

	@Autowired
	private IInstructorService instructorService;

	@Autowired
	private IPersonaService personaService;

	@Autowired
	private IDisciplinaService disciplinaService;

	@Autowired
	private ISesionService sesionService;

	@Autowired
	private ISemestreService semestreService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IInscripcionService inscripcionService;

	@Autowired
	private IAsistenciaService asistenciaService;

	@Autowired
	private IActividadService actividadService;

	@Value("${upload.path}")
	private String uploadPath;

	@GetMapping("/inicio")
	public String inicio(Authentication auth, Model model) {

		var usuario = usuarioService.buscarPorUsername(auth.getName());
		List<Map<String, Object>> sesionesData = new java.util.ArrayList<>();

		List<Actividad> misActividades = new java.util.ArrayList<>();
		Integer miInstructorId = null;

		if (usuario != null) {
			Integer personaId = usuario.getPersona().getId();

			miInstructorId = instructorService.buscarInstructoresActivos().stream()
				.filter(i -> i.getPersona().getId().equals(personaId))
				.findFirst()
				.map(i -> i.getId())
				.orElse(null);

			List<Sesion> todasHoy = sesionService.buscarSesionesPorFecha(LocalDate.now());

			List<Sesion> sesionesHoy = todasHoy.stream()
				.filter(s -> s.getHorario().getActividad()
							  .getInstructor().getPersona().getId()
							  .equals(personaId)
						  && !"REALIZADA".equals(s.getEstatus()))
				.toList();

			for (Sesion sesion : sesionesHoy) {
				List<Inscripcion> inscritos = inscripcionService
					.buscarInscripcionesPorActividad(sesion.getHorario().getActividad())
					.stream()
					.filter(i -> "APROBADA".equals(i.getEstatusSolicitud()))
					.toList();

				List<Map<String, Object>> alumnosData = new java.util.ArrayList<>();
				for (Inscripcion ins : inscritos) {
					long acumuladas = asistenciaService
						.contarAsistenciasPorAlumnoYActividad(
							ins.getAlumno(),
							sesion.getHorario().getActividad());

					Map<String, Object> alumnoMap = new java.util.HashMap<>();
					alumnoMap.put("inscripcion", ins);
					alumnoMap.put("acumuladas", acumuladas);
					alumnosData.add(alumnoMap);
				}

				Map<String, Object> sesionMap = new java.util.HashMap<>();
				sesionMap.put("sesion", sesion);
				sesionMap.put("alumnos", alumnosData);
				sesionesData.add(sesionMap);
			}

			misActividades = actividadService.buscarActividadesActivas().stream()
				.filter(a -> a.getInstructor() != null
						  && a.getInstructor().getPersona() != null
						  && a.getInstructor().getPersona().getId().equals(personaId))
				.collect(Collectors.toList());
		}

		model.addAttribute("sesionesData", sesionesData);
		model.addAttribute("misActividades", misActividades);
		model.addAttribute("miInstructorId", miInstructorId);
		model.addAttribute("hoy", LocalDate.now());
		model.addAttribute("semestre", semestreService.buscarSemestreActivo());
		return "instructor/dashboard";
	}

	@GetMapping("/instructores")
	public String listar(@RequestParam(required = false) String nombre, Model model, Authentication auth) {

		boolean isAdmin = auth.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		List<Instructor> instructores;
		if (nombre != null && !nombre.trim().isEmpty()) {
			if (isAdmin) {
				String n = nombre.trim().toLowerCase();
				instructores = instructorService.buscarTodosInstructores().stream()
					.filter(i -> i.getPersona().getNombre().toLowerCase().contains(n)
						|| i.getPersona().getApellido().toLowerCase().contains(n))
					.collect(Collectors.toList());
			} else {
				instructores = instructorService.buscarActivosPorNombre(nombre.trim());
			}
		} else {
			instructores = isAdmin
				? instructorService.buscarTodosInstructores()
				: instructorService.buscarInstructoresActivos();
		}

		model.addAttribute("instructores", instructores);
		model.addAttribute("nombre", nombre);
		return "instructor/listaInstructores";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("instructor", new Instructor());
		model.addAttribute("persona", new Persona());
		model.addAttribute("disciplinas", disciplinaService.buscarTodasDisciplinas());
		return "instructor/formularioInstructor";
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Instructor instructor,
	                      @ModelAttribute Persona persona,
	                      @RequestParam("fotoArchivo") MultipartFile archivo,
	                      @RequestParam(required = false) String fotoActual,
	                      RedirectAttributes attributes) {

		if (persona.getTelefono() != null && !persona.getTelefono().isEmpty()) {
			if (!persona.getTelefono().matches("[0-9]{10}")) {
				attributes.addFlashAttribute("msg",
					"⚠ El teléfono debe tener exactamente 10 dígitos numéricos.");
				return "redirect:/instructor/instructores";
			}
		}

		if (persona.getNombre() != null)
			persona.setNombre(capitalizarPalabras(persona.getNombre()));
		if (persona.getApellido() != null)
			persona.setApellido(capitalizarPalabras(persona.getApellido()));

		if (!archivo.isEmpty()) {
			try {
				String nombreArchivo = archivo.getOriginalFilename();
				Path ruta = Paths.get(uploadPath + "/instructor/" + nombreArchivo);
				Files.createDirectories(ruta.getParent());
				Files.copy(archivo.getInputStream(), ruta,
					StandardCopyOption.REPLACE_EXISTING);
				instructor.setFoto(nombreArchivo);
			} catch (Exception e) {
				instructor.setFoto("no-image.jpg");
			}
		} else {
			instructor.setFoto(fotoActual != null && !fotoActual.isEmpty()
				? fotoActual : "no-image.jpg");
		}

		personaService.guardarPersona(persona);
		instructor.setPersona(persona);
		if (instructor.getId() == null) {
			instructor.setActivo(1);
		}
		instructorService.guardarInstructor(instructor);
		attributes.addFlashAttribute("msg", "Instructor guardado correctamente.");
		return "redirect:/instructor/instructores";
	}

	private String capitalizarPalabras(String texto) {
		if (texto == null || texto.isEmpty()) return texto;
		String[] palabras = texto.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String palabra : palabras) {
			if (!palabra.isEmpty()) {
				sb.append(Character.toUpperCase(palabra.charAt(0)))
				  .append(palabra.substring(1).toLowerCase())
				  .append(" ");
			}
		}
		return sb.toString().trim();
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Instructor instructor = instructorService.buscarPorId(id);
		model.addAttribute("instructor", instructor);
		return "instructor/detalleInstructor";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Instructor instructor = instructorService.buscarPorId(id);
		model.addAttribute("instructor", instructor);
		model.addAttribute("persona", instructor.getPersona());
		model.addAttribute("disciplinas", disciplinaService.buscarTodasDisciplinas());
		return "instructor/formularioInstructor";
	}

	@GetMapping("/buscarSugerencias")
	@ResponseBody
	public List<Map<String, String>> buscarSugerencias(@RequestParam String q) {
		if (q == null || q.trim().length() < 2) return List.of();
		return instructorService.buscarActivosPorNombre(q.trim()).stream()
				.limit(8)
				.map(i -> {
					Map<String, String> m = new HashMap<>();
					m.put("texto", i.getPersona().getNombre() + " " + i.getPersona().getApellido());
					m.put("sub", i.getDisciplina() != null ? i.getDisciplina().getNombre() : "");
					return m;
				})
				.collect(Collectors.toList());
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Instructor instructor = instructorService.eliminarPorId(id);
		if (instructor == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el instructor.");
		} else {
			attributes.addFlashAttribute("msg", "Instructor eliminado correctamente.");
		}
		return "redirect:/instructor/instructores";
	}
}
