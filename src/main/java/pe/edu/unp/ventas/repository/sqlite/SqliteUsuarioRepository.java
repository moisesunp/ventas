package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.repository.UsuarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteUsuarioRepository implements UsuarioRepository {
    private final DatabaseManager database;

    public SqliteUsuarioRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        String sql = "INSERT INTO usuario(nombre, username, password_hash, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getRol().name());
            ps.setInt(5, usuario.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.asignarId(keys.getLong(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el usuario", e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre=?, password_hash=?, rol=?, activo=? WHERE id=?";
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getRol().name());
            ps.setInt(4, usuario.isActivo() ? 1 : 0);
            ps.setLong(5, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el usuario", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return buscarUno("SELECT * FROM usuario WHERE id=?", id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return buscarUno("SELECT * FROM usuario WHERE username=?", username);
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuario ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los usuarios", e);
        }
    }

    private Optional<Usuario> buscarUno(String sql, Object parametro) {
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el usuario", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Rol.valueOf(rs.getString("rol")),
                rs.getInt("activo") == 1
        );
    }
}
