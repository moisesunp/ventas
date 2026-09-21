package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Producto;
import pe.edu.unp.ventas.repository.ProductoRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteProductoRepository implements ProductoRepository {
    private final DatabaseManager database;

    public SqliteProductoRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public Producto guardar(Producto producto) {
        String sql = "INSERT INTO producto(codigo, nombre, precio_centimos, stock, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setLong(3, aCentimos(producto.getPrecio()));
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    producto.asignarId(keys.getLong(1));
                }
            }
            return producto;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el producto", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET nombre=?, precio_centimos=?, stock=?, activo=? WHERE id=?";
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setLong(2, aCentimos(producto.getPrecio()));
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.isActivo() ? 1 : 0);
            ps.setLong(5, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el producto", e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return buscarUno("SELECT * FROM producto WHERE id=?", id);
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT * FROM producto WHERE codigo=?", codigo);
    }

    @Override
    public List<Producto> listar() {
        return listarConSql("SELECT * FROM producto ORDER BY nombre");
    }

    @Override
    public List<Producto> listarActivos() {
        return listarConSql("SELECT * FROM producto WHERE activo=1 ORDER BY nombre");
    }

    private Optional<Producto> buscarUno(String sql, Object parametro) {
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el producto", e);
        }
    }

    private List<Producto> listarConSql(String sql) {
        List<Producto> productos = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
            return productos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los productos", e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getLong("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                BigDecimal.valueOf(rs.getLong("precio_centimos"), 2),
                rs.getInt("stock"),
                rs.getInt("activo") == 1
        );
    }

    private long aCentimos(BigDecimal valor) {
        return valor.movePointRight(2).longValueExact();
    }
}
