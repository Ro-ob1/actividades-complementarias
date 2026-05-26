package itch.ac.service.implementJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import itch.ac.model.Usuario;
import itch.ac.repository.UsuarioRepository;
import itch.ac.service.IUsuarioService;

@Primary
@Service
public class UsuarioServiceJPA implements IUsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public List<Usuario> buscarTodosUsuarios() {
		return usuarioRepository.findAll();
	}

	@Override
	public List<Usuario> buscarUsuariosActivos() {
		return usuarioRepository.findByActivo(1);
	}

	@Override
	public void guardarUsuario(Usuario usuario) {
		usuarioRepository.save(usuario);
	}

	@Override
	public Usuario buscarPorId(Integer id) {
		Optional<Usuario> optional = usuarioRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Usuario buscarPorUsername(String username) {
		return usuarioRepository.findByUsername(username);
	}

	@Override
	public Usuario eliminarPorId(Integer id) {
		Optional<Usuario> optional = usuarioRepository.findById(id);
		if (optional.isPresent()) {
			Usuario usuario = optional.get();
			usuario.setActivo(0);
			usuarioRepository.save(usuario);
			return usuario;
		}
		return null;
	}

	@Override
	public boolean existePorUsername(String username, Integer idExcluir) {
		return usuarioRepository.findAll().stream().filter(u -> idExcluir == null || !u.getId().equals(idExcluir))
				.anyMatch(u -> u.getUsername().equalsIgnoreCase(username.trim()));
	}

}