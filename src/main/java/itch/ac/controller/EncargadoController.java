package itch.ac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Encargado;
import itch.ac.model.Inscripcion;
import itch.ac.model.Persona;
import itch.ac.service.IActividadService;
import itch.ac.service.IEncargadoService;
import itch.ac.service.IInscripcionService;
import itch.ac.service.IPersonaService;
import itch.ac.service.ISemestreService;

@Controller
@RequestMapping("/encargado")
public class EncargadoController {

	@Autowired
	private IEncargadoService encargadoService;

	@Autowired
	private IPersonaService personaService;
	@Autowired
	private IInscripcionService inscripcionService;
	@Autowired
	private IActividadService actividadService;
	@Autowired
	private ISemestreService semestreService;

	@GetMapping("/inicio")
	public String inicio(Model model) {

		List<Inscripcion> pendientes = inscripcionService.buscarInscripcionesPorEstatus("PENDIENTE");

		long totalActividades = actividadService.buscarTodasActividades().size();
		long totalInscritos = inscripcionService.buscarTodasInscripciones().stream()
				.filter(i -> "APROBADA".equals(i.getEstatusSolicitud())).count();

		model.addAttribute("pendientes", pendientes);
		model.addAttribute("totalActividades", totalActividades);
		model.addAttribute("totalInscritos", totalInscritos);
		model.addAttribute("totalPendientes", pendientes.size());
		model.addAttribute("semestre", semestreService.buscarSemestreActivo());
		return "encargado/dashboard";
	}

	@GetMapping("/encargados")
	public String listar(Model model) {
		List<Encargado> encargados = encargadoService.buscarEncargadosActivos();
		model.addAttribute("encargados", encargados);
		return "encargado/listaEncargados";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("encargado", new Encargado());
		model.addAttribute("persona", new Persona());
		return "encargado/formularioEncargado";
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Encargado encargado, @ModelAttribute Persona persona,
			RedirectAttributes attributes) {

		// Validar teléfono ANTES de guardar
		if (persona.getTelefono() != null && !persona.getTelefono().isEmpty()) {
			if (!persona.getTelefono().matches("[0-9]{10}")) {
				attributes.addFlashAttribute("msg", "⚠ El teléfono debe tener exactamente 10 dígitos numéricos.");
				return "redirect:/encargado/encargados";
			}
		}

		// Capitalizar
		if (persona.getNombre() != null)
			persona.setNombre(capitalizarPalabras(persona.getNombre()));
		if (persona.getApellido() != null)
			persona.setApellido(capitalizarPalabras(persona.getApellido()));

		personaService.guardarPersona(persona);
		encargado.setPersona(persona);
		if (encargado.getId() == null) {
			encargado.setActivo(1);
		}
		encargadoService.guardarEncargado(encargado);
		attributes.addFlashAttribute("msg", "Encargado guardado correctamente.");
		return "redirect:/encargado/encargados";
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
		Encargado encargado = encargadoService.buscarPorId(id);
		model.addAttribute("encargado", encargado);
		return "encargado/detalleEncargado";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Encargado encargado = encargadoService.buscarPorId(id);
		model.addAttribute("encargado", encargado);
		model.addAttribute("persona", encargado.getPersona());
		return "encargado/formularioEncargado";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Encargado encargado = encargadoService.eliminarPorId(id);
		if (encargado == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el encargado.");
		} else {
			attributes.addFlashAttribute("msg", "Encargado eliminado correctamente.");
		}
		return "redirect:/encargado/encargados";
	}
}