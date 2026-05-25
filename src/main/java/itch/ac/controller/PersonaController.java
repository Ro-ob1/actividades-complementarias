package itch.ac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Persona;
import itch.ac.service.IPersonaService;

@Controller
@RequestMapping("/persona")
public class PersonaController {

	@Autowired
	private IPersonaService personaService;

	@GetMapping("/personas")
	public String listar(Model model) {
		List<Persona> personas = personaService.buscarTodasPersonas();
		model.addAttribute("personas", personas);
		return "persona/listaPersonas";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("persona", new Persona());
		return "persona/formularioPersona";
	}

	@PostMapping("/guardar")
	public String guardar(Persona persona, RedirectAttributes attributes) {
		if (persona.getEmail() != null && !persona.getEmail().isEmpty()
				&& personaService.existePorEmail(persona.getEmail(), persona.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe una persona registrada con ese email.");
			return "redirect:/persona/personas";
		}
		if (persona.getTelefono() != null && !persona.getTelefono().isEmpty()) {
			if (!persona.getTelefono().matches("[0-9]{10}")) {
				attributes.addFlashAttribute("msg", "⚠ El teléfono debe tener exactamente 10 dígitos numéricos.");
				return "redirect:/persona/personas";
			}
		}
		if (persona.getNombre() != null)
			persona.setNombre(capitalizarPalabras(persona.getNombre()));
		if (persona.getApellido() != null)
			persona.setApellido(capitalizarPalabras(persona.getApellido()));
		personaService.guardarPersona(persona);
		attributes.addFlashAttribute("msg", "Persona guardada correctamente.");
		return "redirect:/persona/personas";
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
		Persona persona = personaService.buscarPorId(id);
		model.addAttribute("persona", persona);
		return "persona/detallePersona";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Persona persona = personaService.buscarPorId(id);
		model.addAttribute("persona", persona);
		return "persona/formularioPersona";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Persona persona = personaService.eliminarPorId(id);
		if (persona == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar la persona.");
		} else {
			attributes.addFlashAttribute("msg", "Persona eliminada correctamente.");
		}
		return "redirect:/persona/personas";
	}
}