package pe.edu.unp.ventas.service;

import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.repository.UsuarioRepository;

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioRepository.buscarPorUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

        if (!usuario.isActivo() || !passwordHasher.verificar(password, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }
        return usuario;
    }
}
