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

import itch.ac.model.Disciplina;
import itch.ac.service.IDisciplinaService;

@Controller
@RequestMapping("/disciplina")
public class DisciplinaController {

	@Autowired
	private IDisciplinaService disciplinaService;

	@GetMapping("/disciplinas")
	public String listar(Model model) {
		List<Disciplina> disciplinas = disciplinaService.buscarTodasDisciplinas();
		model.addAttribute("disciplinas", disciplinas);
		return "disciplina/listaDisciplinas";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("disciplina", new Disciplina());
		return "disciplina/formularioDisciplina";
	}

	@PostMapping("/guardar")
	public String guardar(Disciplina disciplina, RedirectAttributes attributes) {
		if (disciplinaService.existePorNombre(disciplina.getNombre(), disciplina.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe una disciplina con ese nombre.");
			return "redirect:/disciplina/disciplinas";
		}
		disciplinaService.guardarDisciplina(disciplina);
		attributes.addFlashAttribute("msg", "Disciplina guardada correctamente.");
		return "redirect:/disciplina/disciplinas";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Disciplina disciplina = disciplinaService.buscarPorId(id);
		model.addAttribute("disciplina", disciplina);
		return "disciplina/detalleDisciplina";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Disciplina disciplina = disciplinaService.buscarPorId(id);
		model.addAttribute("disciplina", disciplina);
		return "disciplina/formularioDisciplina";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Disciplina disciplina = disciplinaService.eliminarPorId(id);
		if (disciplina == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar la disciplina.");
		} else {
			attributes.addFlashAttribute("msg", "Disciplina eliminada correctamente.");
		}
		return "redirect:/disciplina/disciplinas";
	}
}
