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

import itch.ac.model.Espacio;
import itch.ac.service.IEspacioService;

@Controller
@RequestMapping("/espacio")
public class EspacioController {

	@Autowired
	private IEspacioService espacioService;

	@GetMapping("/espacios")
	public String listar(Model model) {
		List<Espacio> espacios = espacioService.buscarTodosEspacios();
		model.addAttribute("espacios", espacios);
		return "espacio/listaEspacios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("espacio", new Espacio());
		return "espacio/formularioEspacio";
	}

	@PostMapping("/guardar")
	public String guardar(Espacio espacio, RedirectAttributes attributes) {
		if (espacioService.existePorNombre(espacio.getNombre(), espacio.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un espacio con ese nombre.");
			return "redirect:/espacio/espacios";
		}
		if (espacio.getNombre() != null)
			espacio.setNombre(capitalizarPalabras(espacio.getNombre()));
		if (espacio.getDescripcion() != null && !espacio.getDescripcion().isEmpty())
			espacio.setDescripcion(capitalizarPalabras(espacio.getDescripcion()));
		espacioService.guardarEspacio(espacio);
		attributes.addFlashAttribute("msg", "Espacio guardado correctamente.");
		return "redirect:/espacio/espacios";
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
		Espacio espacio = espacioService.buscarPorId(id);
		model.addAttribute("espacio", espacio);
		return "espacio/detalleEspacio";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Espacio espacio = espacioService.buscarPorId(id);
		model.addAttribute("espacio", espacio);
		return "espacio/formularioEspacio";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		try {
			Espacio espacio = espacioService.eliminarPorId(id);
			if (espacio == null) {
				attributes.addFlashAttribute("msg", "⚠ No se encontró el espacio.");
			} else {
				attributes.addFlashAttribute("msg", "Espacio eliminado correctamente.");
			}
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			attributes.addFlashAttribute("msg", "⚠ No se puede eliminar: hay horarios asignados a este espacio.");
		}
		return "redirect:/espacio/espacios";
	}
}