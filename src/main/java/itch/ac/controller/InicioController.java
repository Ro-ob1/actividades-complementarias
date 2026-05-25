package itch.ac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import itch.ac.model.Actividad;
import itch.ac.model.Semestre;
import itch.ac.service.IActividadService;
import itch.ac.service.ISemestreService;

@Controller
public class InicioController {

	@Autowired
	private IActividadService actividadService;

	@Autowired
	private ISemestreService semestreService;

	@GetMapping("/")
	public String inicio(Authentication auth,
	                     @RequestParam(required = false) String nombre,
	                     Model model) {

	    if (auth != null && auth.isAuthenticated()
	            && !auth.getName().equals("anonymousUser")) {

	        String rol = auth.getAuthorities().stream()
	                .findFirst().map(a -> a.getAuthority()).orElse("");

	        if ("ROLE_ENCARGADO".equals(rol)) return "redirect:/encargado/inicio";
	        if ("ROLE_INSTRUCTOR".equals(rol)) return "redirect:/instructor/inicio";
	        if ("ROLE_ALUMNO".equals(rol))     return "redirect:/alumno/inicio";
	    }

	    Semestre semestreActivo = semestreService.buscarSemestreActivo();
	    List<Actividad> actividades;

	    if (nombre != null && !nombre.trim().isEmpty()) {
	        actividades = actividadService.buscarPorNombre(nombre);
	    } else {
	        actividades = semestreActivo != null
	                ? actividadService.buscarActividadesActivasPorSemestre(semestreActivo)
	                : actividadService.buscarTodasActividades();
	    }

	    model.addAttribute("actividades", actividades);
	    model.addAttribute("semestre", semestreActivo);
	    model.addAttribute("nombre", nombre);
	    return "inicio";
	}
	@GetMapping("/login")
	public String mostrarLogin() {
		return "login";
	}
}