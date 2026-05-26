package itch.ac.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Actividad;
import itch.ac.model.Alumno;
import itch.ac.model.Inscripcion;
import itch.ac.model.Persona;
import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.IAlumnoService;
import itch.ac.service.ICarreraService;
import itch.ac.service.IConstanciaService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.IPersonaService;
import itch.ac.service.ISemestreService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/alumno")
public class AlumnoController {

	@Autowired
	private IAlumnoService alumnoService;

	@Autowired
	private IPersonaService personaService;

	@Autowired
	private ICarreraService carreraService;
	@Autowired
	private IInscripcionService inscripcionService;
	@Autowired
	private ISemestreService semestreService;
	@Autowired
	private IActividadService actividadService;

	@Autowired
	private IConstanciaService constanciaService;

	@Value("${upload.path}")
	private String uploadPath;

	@GetMapping("/inicio")
	public String inicio(Authentication auth, Model model) {

		Alumno alumno = alumnoService.buscarPorNumControl(auth.getName());
		Semestre semestre = semestreService.buscarSemestreActivo();

		List<Inscripcion> inscripciones = List.of();
		int creditos = 0;

		List<Map<String, String>> notificaciones = new ArrayList<>();

		if (alumno != null) {
			inscripciones = inscripcionService.buscarInscripcionesPorAlumno(alumno);
			final Integer alumnoId = alumno.getId();

			creditos = constanciaService.buscarTodasConstancias().stream()
					.filter(c -> c.getInscripcion().getAlumno().getId().equals(alumnoId)
							&& "ENTREGADA".equals(c.getEstatus()))
					.mapToInt(c -> c.getValorCurricular() != null ? c.getValorCurricular() : 0)
					.sum();

			// Notificaciones: solicitudes rechazadas (puede volver a solicitar)
			inscripciones.stream()
					.filter(i -> "RECHAZADA".equals(i.getEstatusSolicitud()))
					.forEach(i -> {
						Map<String, String> n = new HashMap<>();
						n.put("tipo", "danger");
						n.put("mensaje", "Tu solicitud para \"" + i.getActividad().getNombre() + "\" fue rechazada"
								+ (i.getMotivoRechazo() != null && !i.getMotivoRechazo().isEmpty()
										? ": " + i.getMotivoRechazo()
										: "")
								+ ". Puedes solicitar una nueva actividad.");
						notificaciones.add(n);
					});

			// Notificaciones: constancias listas para recoger
			constanciaService.buscarTodasConstancias().stream()
					.filter(c -> c.getInscripcion().getAlumno().getId().equals(alumnoId)
							&& "GENERADA".equals(c.getEstatus()))
					.forEach(c -> {
						Map<String, String> n = new HashMap<>();
						n.put("tipo", "info");
						n.put("mensaje", "Tu constancia de \"" + c.getInscripcion().getActividad().getNombre()
								+ "\" está lista. Acude con el encargado para recibirla.");
						notificaciones.add(n);
					});
		}

		model.addAttribute("alumno", alumno);
		model.addAttribute("inscripciones", inscripciones);
		model.addAttribute("creditos", creditos);
		model.addAttribute("notificaciones", notificaciones);
		model.addAttribute("semestre", semestre);
		return "alumno/dashboard";
	}

	@GetMapping("/catalogo")
	public String catalogo(Authentication auth, @RequestParam(required = false) String nombre, Model model) {

		Semestre semestre = semestreService.buscarSemestreActivo();
		List<Actividad> actividades;

		if (nombre != null && !nombre.trim().isEmpty()) {
			actividades = actividadService.buscarPorNombre(nombre);
		} else {
			actividades = semestre != null ? actividadService.buscarActividadesActivasPorSemestre(semestre)
					: actividadService.buscarTodasActividades();
		}

		// Buscar el alumno autenticado para preseleccionarlo en inscripción
		Alumno alumno = alumnoService.buscarPorNumControl(auth.getName());

		model.addAttribute("actividades", actividades);
		model.addAttribute("semestre", semestre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("alumno", alumno);
		return "alumno/catalogo";
	}

	@GetMapping("/solicitar")
	public String solicitar(@RequestParam Integer idActividad, Authentication auth, Model model) {

		Alumno alumno = alumnoService.buscarPorNumControl(auth.getName());
		Actividad actividad = actividadService.buscarPorId(idActividad);

		model.addAttribute("alumno", alumno);
		model.addAttribute("actividad", actividad);
		model.addAttribute("inscripcion", new Inscripcion());
		return "alumno/solicitudInscripcion";
	}

	@PostMapping("/solicitar")
	public String guardarSolicitud(@RequestParam Integer idActividad, Authentication auth,
	        @RequestParam(required = false) MultipartFile archivoHorarioPdf, RedirectAttributes attributes) {

	    System.out.println("=== DIAGNÓSTICO GUARDAR SOLICITUD ===");
	    System.out.println("1. idActividad: " + idActividad);
	    System.out.println("2. auth: " + auth);
	    System.out.println("3. auth.getName(): " + (auth != null ? auth.getName() : "null"));
	    
	    // Buscar alumno
	    Alumno alumno = alumnoService.buscarPorNumControl(auth.getName());
	    System.out.println("4. Alumno encontrado: " + (alumno != null ? alumno.getId() + " - " + alumno.getNumControl() : "NULL"));
	    
	    if (alumno == null) {
	        attributes.addFlashAttribute("msg", "Error: No se encontró el alumno con número de control: " + auth.getName());
	        return "redirect:/alumno/catalogo";
	    }
	    
	    // Buscar actividad
	    Actividad actividad = actividadService.buscarPorId(idActividad);
	    System.out.println("5. Actividad encontrada: " + (actividad != null ? actividad.getId() + " - " + actividad.getNombre() : "NULL"));
	    
	    if (actividad == null) {
	        attributes.addFlashAttribute("msg", "Error: No se encontró la actividad con ID: " + idActividad);
	        return "redirect:/alumno/catalogo";
	    }

	    // RF-13: un alumno solo puede tener una inscripción activa (PENDIENTE o APROBADA) por semestre
	    Semestre semestreActividad = actividad.getSemestre();
	    if (inscripcionService.alumnoTieneInscripcionActiva(alumno, semestreActividad)) {
	        attributes.addFlashAttribute("msg",
	                "⚠ Ya tienes una inscripción activa en este semestre. Puedes volver a solicitar solo si tu solicitud anterior fue rechazada.");
	        return "redirect:/alumno/catalogo";
	    }

	    // Crear inscripción
	    Inscripcion inscripcion = new Inscripcion();
	    inscripcion.setAlumno(alumno);
	    inscripcion.setActividad(actividad);
	    inscripcion.setEstatusSolicitud("PENDIENTE");
	    inscripcion.setFechaSolicitud(LocalDate.now());
	    System.out.println("6. Inscripción creada con estatus: PENDIENTE");
	    
	    // Guardar PDF
	    if (archivoHorarioPdf != null && !archivoHorarioPdf.isEmpty()) {
	        System.out.println("7. Archivo recibido: " + archivoHorarioPdf.getOriginalFilename());
	        System.out.println("8. Tamaño: " + archivoHorarioPdf.getSize() + " bytes");
	        try {
	            String nombre = System.currentTimeMillis() + "_" + archivoHorarioPdf.getOriginalFilename();
	            Path ruta = Paths.get(uploadPath + "/inscripcion/" + nombre);
	            Files.createDirectories(ruta.getParent());
	            Files.copy(archivoHorarioPdf.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
	            inscripcion.setArchivoHorario(nombre);
	            System.out.println("9. Archivo guardado en: " + ruta.toString());
	        } catch (Exception e) {
	            System.out.println("10. ERROR al guardar archivo: " + e.getMessage());
	            e.printStackTrace();
	            inscripcion.setArchivoHorario(null);
	        }
	    } else {
	        System.out.println("7. No se recibió archivo PDF");
	    }
	    
	    // Guardar inscripción
	    try {
	        inscripcionService.guardarInscripcion(inscripcion);
	        System.out.println("11. Inscripción guardada exitosamente con ID: " + inscripcion.getId());
	        attributes.addFlashAttribute("msg", "Solicitud enviada correctamente. En espera de aprobación.");
	    } catch (Exception e) {
	        System.out.println("11. ERROR al guardar inscripción: " + e.getMessage());
	        e.printStackTrace();
	        attributes.addFlashAttribute("msg", "Error al guardar la solicitud: " + e.getMessage());
	        return "redirect:/alumno/catalogo";
	    }
	    
	    return "redirect:/alumno/inicio";
	}

	@GetMapping("/alumnos")
	public String listar(@RequestParam(required = false) String numControl,
	                     @RequestParam(required = false) String nombre,
	                     Model model, Authentication auth) {

		boolean isAdmin = auth.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		List<Alumno> alumnos;

		if (numControl != null && !numControl.trim().isEmpty()) {
			Alumno alumno = alumnoService.buscarPorNumControl(numControl.trim());
			if (!isAdmin && alumno != null && !Integer.valueOf(1).equals(alumno.getActivo())) {
				alumno = null;
			}
			alumnos = alumno != null ? List.of(alumno) : List.of();
		} else if (nombre != null && !nombre.trim().isEmpty()) {
			if (isAdmin) {
				String n = nombre.trim().toLowerCase();
				alumnos = alumnoService.buscarTodosAlumnos().stream()
					.filter(a -> a.getPersona().getNombre().toLowerCase().contains(n)
						|| a.getPersona().getApellido().toLowerCase().contains(n))
					.collect(Collectors.toList());
			} else {
				alumnos = alumnoService.buscarActivosPorNombre(nombre.trim());
			}
		} else {
			alumnos = isAdmin ? alumnoService.buscarTodosAlumnos() : alumnoService.buscarAlumnosActivos();
		}

		model.addAttribute("alumnos", alumnos);
		model.addAttribute("numControl", numControl);
		model.addAttribute("nombre", nombre);
		return "alumno/listaAlumnos";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("alumno", new Alumno());
		model.addAttribute("persona", new Persona());
		model.addAttribute("carreras", carreraService.buscarTodasCarreras());
		return "alumno/formularioAlumno";
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Alumno alumno, @ModelAttribute Persona persona,
			RedirectAttributes attributes) {
		// Capitalizar
		if (persona.getNombre() != null)
			persona.setNombre(capitalizarPalabras(persona.getNombre()));
		if (persona.getApellido() != null)
			persona.setApellido(capitalizarPalabras(persona.getApellido()));
		
		if (persona.getTelefono() != null && !persona.getTelefono().isEmpty()) {
		    if (!persona.getTelefono().matches("[0-9]{10}")) {
		        attributes.addFlashAttribute("msg",
		            "⚠ El teléfono debe tener exactamente 10 dígitos numéricos.");
		        return "redirect:/alumno/alumnos";
		    }
		}
		
		if (alumno.getNumControl() == null
		        || !alumno.getNumControl().matches("[0-9]{8,10}")) {
		    attributes.addFlashAttribute("msg",
		        "⚠ El número de control debe tener entre 8 y 10 dígitos numéricos.");
		    return "redirect:/alumno/alumnos";
		}
		
		if (alumnoService.existePorNumControl(alumno.getNumControl(), alumno.getId())) {
		    attributes.addFlashAttribute("msg",
		        "⚠ Ya existe un alumno con ese número de control.");
		    return "redirect:/alumno/alumnos";
		}

		personaService.guardarPersona(persona);
		alumno.setPersona(persona);
		if (alumno.getId() == null) {
			alumno.setActivo(1);
		}
		alumnoService.guardarAlumno(alumno);
		attributes.addFlashAttribute("msg", "Alumno guardado correctamente.");
		return "redirect:/alumno/alumnos";
	}

	private String capitalizarPalabras(String texto) {
		if (texto == null || texto.isEmpty())
			return texto;
		String[] palabras = texto.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String palabra : palabras) {
			if (!palabra.isEmpty()) {
				sb.append(Character.toUpperCase(palabra.charAt(0))).append(palabra.substring(1).toLowerCase())
						.append(" ");
			}
		}
		return sb.toString().trim();
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Alumno alumno = alumnoService.buscarPorId(id);
		model.addAttribute("alumno", alumno);
		return "alumno/detalleAlumno";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Alumno alumno = alumnoService.buscarPorId(id);
		model.addAttribute("alumno", alumno);
		model.addAttribute("persona", alumno.getPersona());
		model.addAttribute("carreras", carreraService.buscarTodasCarreras());
		return "alumno/formularioAlumno";
	}

	@GetMapping("/buscarSugerencias")
	@ResponseBody
	public List<Map<String, String>> buscarSugerencias(@RequestParam String q) {
		if (q == null || q.trim().length() < 2) return List.of();
		return alumnoService.buscarActivosPorNombre(q.trim()).stream()
				.limit(8)
				.map(a -> {
					Map<String, String> m = new HashMap<>();
					m.put("texto", a.getPersona().getNombre() + " " + a.getPersona().getApellido());
					m.put("sub", a.getNumControl());
					return m;
				})
				.collect(Collectors.toList());
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Alumno alumno = alumnoService.eliminarPorId(id);
		if (alumno == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el alumno.");
		} else {
			attributes.addFlashAttribute("msg", "Alumno eliminado correctamente.");
		}
		return "redirect:/alumno/alumnos";
	}
}