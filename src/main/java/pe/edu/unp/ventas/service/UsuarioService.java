package pe.edu.unp.ventas.service;

import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    public Usuario registrar(String nombre, String username, String password, Rol rol) {
        if (usuarioRepository.buscarPorUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        Usuario usuario = new Usuario(nombre, username, passwordHasher.hash(password), rol);
        return usuarioRepository.guardar(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.listar();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public void cambiarRol(Long id, Rol rol) {
        Usuario usuario = buscarPorId(id);
        usuario.cambiarRol(rol);
        usuarioRepository.actualizar(usuario);
    }

    public void activar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.activar();
        usuarioRepository.actualizar(usuario);
    }

    public void desactivar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.desactivar();
        usuarioRepository.actualizar(usuario);
    }
}
