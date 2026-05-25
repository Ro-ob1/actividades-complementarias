package itch.ac.controller;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.CriterioEval;
import itch.ac.service.ICriterioBaseService;
import itch.ac.service.ICriterioEvalService;
import itch.ac.service.IEvaluacionService;
import itch.ac.service.INivelDesempenioService;

@Controller
@RequestMapping("/criterioEval")
public class CriterioEvalController {

	@Autowired
	private ICriterioEvalService criterioEvalService;

	@Autowired
	private IEvaluacionService evaluacionService;

	@Autowired
	private ICriterioBaseService criterioBaseService;

	@Autowired
	private INivelDesempenioService nivelDesempenioService;

	@GetMapping("/criterios")
	public String listar(Model model) {
		model.addAttribute("criterios", criterioEvalService.buscarTodosCriteriosEval());
		return "criterioEval/listaCriteriosEval";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("criterioEval", new CriterioEval());
		model.addAttribute("evaluaciones", evaluacionService.buscarTodasEvaluaciones());
		model.addAttribute("criteriosBase", criterioBaseService.buscarTodosCriterios());
		model.addAttribute("niveles", nivelDesempenioService.buscarTodosNiveles());
		return "criterioEval/formularioCriterioEval";
	}

	@PostMapping("/guardar")
	public String guardar(CriterioEval criterioEval, RedirectAttributes attributes) {
		if (criterioEval.getEvaluacion() != null && criterioEval.getEvaluacion().getId() != null) {
			criterioEval.setEvaluacion(evaluacionService.buscarPorId(criterioEval.getEvaluacion().getId()));
		}
		if (criterioEval.getCriterio() != null && criterioEval.getCriterio().getId() != null) {
			criterioEval.setCriterio(criterioBaseService.buscarPorId(criterioEval.getCriterio().getId()));
		}
		if (criterioEval.getNivel() != null && criterioEval.getNivel().getId() != null) {
			criterioEval.setNivel(nivelDesempenioService.buscarPorId(criterioEval.getNivel().getId()));
		}
		criterioEvalService.guardarCriterioEval(criterioEval);
		attributes.addFlashAttribute("msg", "Criterio de evaluación guardado correctamente.");
		return "redirect:/criterioEval/criterios";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		CriterioEval criterioEval = criterioEvalService.buscarPorId(id);
		model.addAttribute("criterioEval", criterioEval);
		return "criterioEval/detalleCriterioEval";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		CriterioEval criterioEval = criterioEvalService.buscarPorId(id);
		model.addAttribute("criterioEval", criterioEval);
		model.addAttribute("evaluaciones", evaluacionService.buscarTodasEvaluaciones());
		model.addAttribute("criteriosBase", criterioBaseService.buscarTodosCriterios());
		model.addAttribute("niveles", nivelDesempenioService.buscarTodosNiveles());
		return "criterioEval/formularioCriterioEval";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
		CriterioEval criterioEval = criterioEvalService.eliminarPorId(id);
		if (criterioEval == null) {
			attributes.addFlashAttribute("msg", "No se pudo eliminar el criterio.");
		} else {
			attributes.addFlashAttribute("msg", "Criterio eliminado correctamente.");
		}
		return "redirect:/criterioEval/criterios";
	}
}