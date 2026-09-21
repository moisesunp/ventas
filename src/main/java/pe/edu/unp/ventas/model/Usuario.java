package pe.edu.unp.ventas.model;

import java.util.Objects;

public class Usuario {
    private Long id;
    private String nombre;
    private final String username;
    private String passwordHash;
    private Rol rol;
    private boolean activo;

    public Usuario(Long id, String nombre, String username, String passwordHash, Rol rol, boolean activo) {
        this.id = id;
        this.nombre = validarTexto(nombre, "nombre");
        this.username = validarTexto(username, "username");
        this.passwordHash = validarTexto(passwordHash, "passwordHash");
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
        this.activo = activo;
    }

    public Usuario(String nombre, String username, String passwordHash, Rol rol) {
        this(null, nombre, username, passwordHash, rol, true);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }

    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El usuario ya tiene un id asignado");
        }
        this.id = Objects.requireNonNull(id);
    }

    public void cambiarNombre(String nombre) {
        this.nombre = validarTexto(nombre, "nombre");
    }

    public void cambiarPassword(String passwordHash) {
        this.passwordHash = validarTexto(passwordHash, "passwordHash");
    }

    public void cambiarRol(Rol rol) {
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
    }

    public void activar() { this.activo = true; }
    public void desactivar() { this.activo = false; }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " es obligatorio");
        }
        return valor.trim();
    }
}
