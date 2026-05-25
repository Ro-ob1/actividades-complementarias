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

import itch.ac.model.CriterioBase;
import itch.ac.service.ICriterioBaseService;

@Controller
@RequestMapping("/criterioBase")
public class CriterioBaseController {

	@Autowired
	private ICriterioBaseService criterioBaseService;

	@GetMapping("/criterios")
	public String listar(Model model) {
		List<CriterioBase> criterios = criterioBaseService.buscarTodosCriterios();
		model.addAttribute("criterios", criterios);
		return "criterioBase/listaCriterios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("criterio", new CriterioBase());
		return "criterioBase/formularioCriterio";
	}

	@PostMapping("/guardar")
	public String guardar(CriterioBase criterio, RedirectAttributes attributes) {
		if (criterioBaseService.existePorNumero(criterio.getNumeroCriterio(), criterio.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un criterio con ese número.");
			return "redirect:/criterioBase/criterios";
		}
		if (criterioBaseService.existePorNombre(criterio.getNombre(), criterio.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un criterio con ese nombre.");
			return "redirect:/criterioBase/criterios";
		}
		if (criterio.getNombre() != null)
			criterio.setNombre(capitalizarPalabras(criterio.getNombre()));
		criterioBaseService.guardarCriterio(criterio);
		attributes.addFlashAttribute("msg", "Criterio guardado correctamente.");
		return "redirect:/criterioBase/criterios";
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
		CriterioBase criterio = criterioBaseService.buscarPorId(id);
		model.addAttribute("criterio", criterio);
		return "criterioBase/detalleCriterio";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		CriterioBase criterio = criterioBaseService.buscarPorId(id);
		model.addAttribute("criterio", criterio);
		return "criterioBase/formularioCriterio";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		CriterioBase criterio = criterioBaseService.eliminarPorId(id);
		if (criterio == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el criterio.");
		} else {
			attributes.addFlashAttribute("msg", "Criterio eliminado correctamente.");
		}
		return "redirect:/criterioBase/criterios";
	}
}