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

import itch.ac.model.NivelDesempenio;
import itch.ac.service.INivelDesempenioService;

@Controller
@RequestMapping("/nivelDesempenio")
public class NivelDesempenioController {

	@Autowired
	private INivelDesempenioService nivelDesempenioService;

	@GetMapping("/niveles")
	public String listar(Model model) {
		List<NivelDesempenio> niveles = nivelDesempenioService.buscarTodosNiveles();
		model.addAttribute("niveles", niveles);
		return "nivelDesempenio/listaNiveles";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("nivel", new NivelDesempenio());
		return "nivelDesempenio/formularioNivel";
	}

	@PostMapping("/guardar")
	public String guardar(NivelDesempenio nivel, RedirectAttributes attributes) {
		if (nivelDesempenioService.existePorNombre(nivel.getNombre(), nivel.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un nivel con ese nombre.");
			return "redirect:/nivelDesempenio/niveles";
		}
		nivelDesempenioService.guardarNivel(nivel);
		attributes.addFlashAttribute("msg", "Nivel guardado correctamente.");
		return "redirect:/nivelDesempenio/niveles";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		NivelDesempenio nivel = nivelDesempenioService.buscarPorId(id);
		model.addAttribute("nivel", nivel);
		return "nivelDesempenio/detalleNivel";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		NivelDesempenio nivel = nivelDesempenioService.buscarPorId(id);
		model.addAttribute("nivel", nivel);
		return "nivelDesempenio/formularioNivel";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		NivelDesempenio nivel = nivelDesempenioService.eliminarPorId(id);
		if (nivel == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el nivel.");
		} else {
			attributes.addFlashAttribute("msg", "Nivel eliminado correctamente.");
		}
		return "redirect:/nivelDesempenio/niveles";
	}
}