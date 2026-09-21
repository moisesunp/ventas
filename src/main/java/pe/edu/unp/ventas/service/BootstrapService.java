package pe.edu.unp.ventas.service;

import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.repository.UsuarioRepository;

public class BootstrapService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public BootstrapService(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    public void crearAdministradorInicial() {
        if (usuarioRepository.listar().isEmpty()) {
            usuarioService.registrar("Administrador", "admin", "admin123", Rol.ADMINISTRADOR);
        }
    }
}
