package itch.ac.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itch.ac.model.Persona;
import itch.ac.model.Usuario;
import itch.ac.service.IAlumnoService;
import itch.ac.service.IEncargadoService;
import itch.ac.service.IInstructorService;
import itch.ac.service.IPersonaService;
import itch.ac.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IPersonaService personaService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IAlumnoService alumnoService;
	@Autowired
	private IInstructorService instructorService;
	@Autowired
	private IEncargadoService encargadoService;

	// Método auxiliar para determinar el rol de una persona
	private String determinarRolPersona(Persona persona) {
		if (persona.getId() == 1) {
			return "ROLE_ADMIN";
		} else if (alumnoService.buscarPorPersona(persona) != null) {
			return "ROLE_ALUMNO";
		} else if (instructorService.buscarPorPersona(persona) != null) {
			return "ROLE_INSTRUCTOR";
		} else if (encargadoService.buscarPorPersona(persona) != null) {
			return "ROLE_ENCARGADO";
		}
		return null;
	}
	
	// Método auxiliar para obtener el nombre amigable del rol
	private String obtenerNombreRolAmigable(String rol) {
		if (rol == null) return "Sin rol asignado";
		switch (rol) {
			case "ROLE_ADMIN": return "Administrador";
			case "ROLE_ALUMNO": return "Alumno";
			case "ROLE_INSTRUCTOR": return "Instructor";
			case "ROLE_ENCARGADO": return "Encargado";
			default: return rol.replace("ROLE_", "");
		}
	}

	@GetMapping("/usuarios")
	public String listar(Model model, Authentication auth) {
		boolean isAdmin = auth.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		List<Usuario> usuarios = isAdmin
			? usuarioService.buscarTodosUsuarios()
			: usuarioService.buscarUsuariosActivos();
		model.addAttribute("usuarios", usuarios);
		return "usuario/listaUsuarios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("usuario", new Usuario());

		// Solo personas que aún no tienen usuario
		Set<Integer> conUsuario = usuarioService.buscarTodosUsuarios().stream()
				.map(u -> u.getPersona().getId())
				.collect(Collectors.toSet());
		List<Persona> personas = personaService.buscarTodasPersonas().stream()
				.filter(p -> !conUsuario.contains(p.getId()))
				.collect(Collectors.toList());
		model.addAttribute("personas", personas);

		Map<Integer, String> rolesAmigablesPorPersona = new HashMap<>();
		for (Persona persona : personas) {
			String rol = determinarRolPersona(persona);
			rolesAmigablesPorPersona.put(persona.getId(),
					rol != null ? obtenerNombreRolAmigable(rol) : "No registrado");
		}
		model.addAttribute("rolesPorPersona", rolesAmigablesPorPersona);
		return "usuario/formularioUsuario";
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {

		// Validar username único
		if (usuarioService.existePorUsername(usuario.getUsername(), usuario.getId())) {
			attributes.addFlashAttribute("msg", "⚠ Ya existe un usuario con ese nombre de usuario.");
			return "redirect:/usuario/usuarios";
		}

		// Username en minúsculas
		if (usuario.getUsername() != null)
			usuario.setUsername(usuario.getUsername().toLowerCase().trim());

		if (usuario.getId() == null) {
			// NUEVO: detectar rol automáticamente según la tabla de la persona
			Persona persona = personaService.buscarPorId(usuario.getPersona().getId());

			if (persona.getId() == 1) {
				usuario.setRol("ROLE_ADMIN");
			} else if (alumnoService.buscarPorPersona(persona) != null) {
				usuario.setRol("ROLE_ALUMNO");
			} else if (instructorService.buscarPorPersona(persona) != null) {
				usuario.setRol("ROLE_INSTRUCTOR");
			} else if (encargadoService.buscarPorPersona(persona) != null) {
				usuario.setRol("ROLE_ENCARGADO");
			} else {
				attributes.addFlashAttribute("msg",
						"⚠ La persona seleccionada no está registrada como alumno, instructor ni encargado.");
				return "redirect:/usuario/usuarios";
			}

			if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
				attributes.addFlashAttribute("msg", "⚠ La contraseña es obligatoria.");
				return "redirect:/usuario/usuarios";
			}
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuario.setActivo(1);

		} else {
			// EDICIÓN: conservar rol y persona del registro original
			Usuario actual = usuarioService.buscarPorId(usuario.getId());
			usuario.setRol(actual.getRol());
			usuario.setPersona(actual.getPersona());

			// No permitir desactivar al admin
			if ("ROLE_ADMIN".equals(actual.getRol()) && Integer.valueOf(0).equals(usuario.getActivo())) {
				attributes.addFlashAttribute("msg", "⚠ No es posible desactivar al administrador.");
				return "redirect:/usuario/usuarios";
			}

			if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
				usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			} else {
				usuario.setPassword(actual.getPassword());
			}
		}

		usuarioService.guardarUsuario(usuario);
		attributes.addFlashAttribute("msg", "Usuario guardado correctamente.");
		return "redirect:/usuario/usuarios";
	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Integer id, Model model) {
		Usuario usuario = usuarioService.buscarPorId(id);
		model.addAttribute("usuario", usuario);
		return "usuario/detalleUsuario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		Usuario usuario = usuarioService.buscarPorId(id);
		model.addAttribute("usuario", usuario);
		model.addAttribute("rolAmigable", obtenerNombreRolAmigable(usuario.getRol()));
		return "usuario/formularioUsuario";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, Authentication auth, RedirectAttributes attributes) {
		Usuario usuarioActual = usuarioService.buscarPorUsername(auth.getName());
		if (usuarioActual != null && usuarioActual.getId().equals(id)) {
			attributes.addFlashAttribute("msg", "⚠ No puedes eliminar tu propio usuario.");
			return "redirect:/usuario/usuarios";
		}
		Usuario usuario = usuarioService.eliminarPorId(id);
		if (usuario == null) {
			attributes.addFlashAttribute("msg", "⚠ No se encontró el usuario.");
		} else {
			attributes.addFlashAttribute("msg", "Usuario eliminado correctamente.");
		}
		return "redirect:/usuario/usuarios";
	}
}