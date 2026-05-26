package itch.ac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import itch.ac.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByUsername(String username);

	java.util.List<Usuario> findByActivo(int activo);

	Usuario findByPersona(itch.ac.model.Persona persona);
}