package pe.edu.unp.ventas.repository;

import pe.edu.unp.ventas.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    void actualizar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorUsername(String username);
    List<Usuario> listar();
}
