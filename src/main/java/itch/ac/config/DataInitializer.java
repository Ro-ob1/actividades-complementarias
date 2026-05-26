package itch.ac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import itch.ac.model.Persona;
import itch.ac.model.Usuario;
import itch.ac.repository.PersonaRepository;
import itch.ac.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		if (usuarioRepository.findByUsername("admin") != null) {
			return;
		}

		Persona persona = new Persona();
		persona.setNombre("Roberta");
		persona.setApellido("Garcia Oropeza");
		personaRepository.save(persona);

		Usuario admin = new Usuario();
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("Admin123!"));
		admin.setRol("ROLE_ADMIN");
		admin.setActivo(1);
		admin.setPersona(persona);
		usuarioRepository.save(admin);

		System.out.println(">>> Usuario admin creado correctamente.");
	}
}
