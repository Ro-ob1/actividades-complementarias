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

import itch.ac.model.Carrera;
import itch.ac.service.ICarreraService;

@Controller
@RequestMapping("/carrera")
public class CarreraController {

	@Autowired
	private ICarreraService carreraService;

	@GetMapping("/carreras")
	public String listar(Model model) {
		List<Carrera> carreras = carreraService.buscarTodasCarreras();
		model.addAttribute("carreras", carreras);
		return "carrera/listaCarreras";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("carrera", new Carrera());
		return "carrera/formularioCarrera";
	}

	@PostMapping("/guardar")
	public String guardar(Carrera carrera, RedirectAttributes attributes) {
		if (carreraService.existePorClave(carrera.getClave(), carrera.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe una carrera con la clave " + carrera.getClave() + ".");
			return "redirect:/carrera/carreras";
		}
		if (carreraService.existePorNombre(carrera.getNombre(), carrera.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe una carrera con ese nombre.");
			return "redirect:/carrera/carreras";
		}
		if (carrera.getClave() != null)
			carrera.setClave(carrera.getClave().toUpperCase());
		if (carrera.getNombre() != null)
			carrera.setNombre(capitalizarPalabras(carrera.getNombre()));
		carreraService.guardarCarrera(carrera);
		attributes.addFlashAttribute("msg", "Carrera guardada correctamente.");
		return "redirect:/carrera/carreras";
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
		Carrera carrera = carreraService.buscarPorId(id);
		model.addAttribute("carrera", carrera);
		return "carrera/detalleCarrera";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Carrera carrera = carreraService.buscarPorId(id);
		model.addAttribute("carrera", carrera);
		return "carrera/formularioCarrera";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Carrera carrera = carreraService.eliminarPorId(id);
		if (carrera == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar la carrera.");
		} else {
			attributes.addFlashAttribute("msg", "Carrera eliminada correctamente.");
		}
		return "redirect:/carrera/carreras";
	}
}