package itch.ac.service;

import java.util.List;
import itch.ac.model.Usuario;

public interface IUsuarioService {
	List<Usuario> buscarTodosUsuarios();

	List<Usuario> buscarUsuariosActivos();

	void guardarUsuario(Usuario usuario);

	Usuario buscarPorId(Integer id);

	Usuario buscarPorUsername(String username);

	Usuario eliminarPorId(Integer id);

	boolean existePorUsername(String username, Integer idExcluir);
}