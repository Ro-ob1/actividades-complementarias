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

import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.ISemestreService;

@Controller
@RequestMapping("/semestre")
public class SemestreController {

	@Autowired
	private ISemestreService semestreService;

	@Autowired
	private IActividadService actividadService;

	@GetMapping("/semestres")
	public String listar(Model model) {
		List<Semestre> semestres = semestreService.buscarTodosSemestres();
		model.addAttribute("semestres", semestres);
		return "semestre/listaSemestres";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("semestre", new Semestre());
		return "semestre/formularioSemestre";
	}

	@PostMapping("/guardar")
	public String guardar(Semestre semestre, RedirectAttributes attributes) {
		if (semestreService.existePorNombre(semestre.getNombre(), semestre.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un semestre con ese nombre.");
			return "redirect:/semestre/semestres";
		}
		if (semestre.getFechaInicio() != null && semestre.getFechaFin() != null
				&& !semestre.getFechaInicio().isBefore(semestre.getFechaFin())) {
			attributes.addFlashAttribute("msg", "⚠ La fecha de inicio debe ser anterior a la fecha de fin.");
			return "redirect:/semestre/semestres";
		}
		if (semestre.getId() == null) {
			Semestre activo = semestreService.buscarSemestreActivo();
			if (activo != null) {
				semestre.setActivo(0);
				semestreService.guardarSemestre(semestre);
				attributes.addFlashAttribute("msg",
					"El semestre \"" + activo.getNombre() + "\" ya está activo. " +
					"El nuevo semestre se registró como inactivo.");
			} else {
				semestre.setActivo(1);
				semestreService.guardarSemestre(semestre);
				attributes.addFlashAttribute("msg", "Semestre guardado correctamente.");
			}
		} else {
			if (Integer.valueOf(1).equals(semestre.getActivo())) {
				// Se está activando este semestre → desactivar el anterior y sus actividades
				Semestre actual = semestreService.buscarSemestreActivo();
				if (actual != null && !actual.getId().equals(semestre.getId())) {
					actividadService.desactivarActividadesPorSemestre(actual);
					actual.setActivo(0);
					semestreService.guardarSemestre(actual);
				}
			} else {
				// Se está desactivando manualmente → desactivar sus actividades también
				Semestre anterior = semestreService.buscarPorId(semestre.getId());
				if (anterior != null && Integer.valueOf(1).equals(anterior.getActivo())) {
					actividadService.desactivarActividadesPorSemestre(anterior);
				}
			}
			semestreService.guardarSemestre(semestre);
			attributes.addFlashAttribute("msg", "Semestre guardado correctamente.");
		}
		return "redirect:/semestre/semestres";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Semestre semestre = semestreService.buscarPorId(id);
		model.addAttribute("semestre", semestre);
		return "semestre/detalleSemestre";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Semestre semestre = semestreService.buscarPorId(id);
		model.addAttribute("semestre", semestre);
		return "semestre/formularioSemestre";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		Semestre semestre = semestreService.eliminarPorId(id);
		if (semestre == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el semestre.");
		} else {
			attributes.addFlashAttribute("msg", "Semestre eliminado correctamente.");
		}
		return "redirect:/semestre/semestres";
	}
}