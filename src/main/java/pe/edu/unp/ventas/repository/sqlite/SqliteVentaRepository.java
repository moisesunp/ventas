package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.*;
import pe.edu.unp.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteVentaRepository implements VentaRepository {
    private final DatabaseManager database;

    public SqliteVentaRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public Venta guardarConfirmada(Venta venta) {
        if (venta.getId() != null) {
            throw new IllegalArgumentException("La venta ya fue persistida");
        }

        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                descontarStock(connection, venta);
                long ventaId = insertarVenta(connection, venta);
                insertarDetalles(connection, ventaId, venta.getDetalles());
                connection.commit();
                venta.asignarId(ventaId);
                return venta;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo confirmar la venta", e);
        }
    }

    @Override
    public void anularConfirmada(Venta venta) {
        if (venta.getId() == null) {
            throw new IllegalArgumentException("La venta no fue persistida");
        }

        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String cambiarEstado =
                        "UPDATE venta SET estado='ANULADA' WHERE id=? AND estado='CONFIRMADA'";

                try (PreparedStatement ps = connection.prepareStatement(cambiarEstado)) {
                    ps.setLong(1, venta.getId());

                    if (ps.executeUpdate() != 1) {
                        throw new IllegalStateException("La venta ya no se encuentra confirmada");
                    }
                }

                String reponer = """
                        UPDATE producto
                        SET stock = stock + (
                            SELECT cantidad
                            FROM detalle_venta
                            WHERE venta_id = ? AND producto_id = producto.id
                        )
                        WHERE id IN (
                            SELECT producto_id
                            FROM detalle_venta
                            WHERE venta_id = ?
                        )
                        """;

                try (PreparedStatement ps = connection.prepareStatement(reponer)) {
                    ps.setLong(1, venta.getId());
                    ps.setLong(2, venta.getId());
                    ps.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo anular la venta", e);
        }
    }

    @Override
    public Optional<Venta> buscarPorId(Long id) {
        String sql = consultaBase() + " WHERE v.id=?";

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapearVenta(connection, rs))
                        : Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la venta", e);
        }
    }

    @Override
    public List<Venta> listar() {
        return listarConSql(
                consultaBase() + " ORDER BY v.fecha DESC",
                null
        );
    }

    @Override
    public List<Venta> listarPorVendedor(Long vendedorId) {
        return listarConSql(
                consultaBase() + " WHERE v.vendedor_id=? ORDER BY v.fecha DESC",
                vendedorId
        );
    }

    private List<Venta> listarConSql(String sql, Long vendedorId) {
        List<Venta> ventas = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (vendedorId != null) {
                ps.setLong(1, vendedorId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(connection, rs));
                }
            }

            return ventas;

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar las ventas", e);
        }
    }

    private long insertarVenta(Connection connection, Venta venta) throws SQLException {
        String sql = """
                INSERT INTO venta(
                    fecha,
                    vendedor_id,
                    subtotal_centimos,
                    tipo_descuento,
                    descuento_centimos,
                    total_centimos,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMADA')
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, venta.getFecha().toString());
            ps.setLong(2, venta.getVendedor().getId());
            ps.setLong(3, aCentimos(venta.getSubtotal()));
            ps.setString(4, venta.getTipoDescuento().name());
            ps.setLong(5, aCentimos(venta.getDescuento()));
            ps.setLong(6, aCentimos(venta.getTotal()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No se obtuvo el id de la venta");
                }
                return keys.getLong(1);
            }
        }
    }

    private void insertarDetalles(
            Connection connection,
            long ventaId,
            List<DetalleVenta> detalles
    ) throws SQLException {

        String sql = """
                INSERT INTO detalle_venta(
                    venta_id,
                    producto_id,
                    cantidad,
                    precio_unitario_centimos,
                    subtotal_centimos
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (DetalleVenta detalle : detalles) {
                ps.setLong(1, ventaId);
                ps.setLong(2, detalle.getProducto().getId());
                ps.setInt(3, detalle.getCantidad());
                ps.setLong(4, aCentimos(detalle.getPrecioUnitario()));
                ps.setLong(5, aCentimos(detalle.getSubtotal()));
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void descontarStock(Connection connection, Venta venta) throws SQLException {
        String sql = """
                UPDATE producto
                SET stock = stock - ?
                WHERE id = ? AND activo = 1 AND stock >= ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (DetalleVenta detalle : venta.getDetalles()) {
                ps.setInt(1, detalle.getCantidad());
                ps.setLong(2, detalle.getProducto().getId());
                ps.setInt(3, detalle.getCantidad());

                if (ps.executeUpdate() != 1) {
                    throw new IllegalStateException(
                            "Stock insuficiente o producto inactivo: "
                                    + detalle.getProducto().getNombre()
                    );
                }
            }
        }
    }

    private Venta mapearVenta(Connection connection, ResultSet rs) throws SQLException {
        Usuario vendedor = new Usuario(
                rs.getLong("u_id"),
                rs.getString("u_nombre"),
                rs.getString("u_username"),
                rs.getString("u_password_hash"),
                Rol.valueOf(rs.getString("u_rol")),
                rs.getInt("u_activo") == 1
        );

        long ventaId = rs.getLong("v_id");
        List<DetalleVenta> detalles = cargarDetalles(connection, ventaId);

        return new Venta(
                ventaId,
                LocalDateTime.parse(rs.getString("v_fecha")),
                vendedor,
                detalles,
                TipoDescuento.valueOf(rs.getString("v_tipo_descuento")),
                desdeCentimos(rs.getLong("v_descuento")),
                EstadoVenta.valueOf(rs.getString("v_estado"))
        );
    }

    private List<DetalleVenta> cargarDetalles(
            Connection connection,
            long ventaId
    ) throws SQLException {

        String sql = """
                SELECT
                    d.id d_id,
                    d.cantidad,
                    d.precio_unitario_centimos,
                    p.id p_id,
                    p.codigo,
                    p.nombre,
                    p.precio_centimos,
                    p.stock,
                    p.activo
                FROM detalle_venta d
                JOIN producto p ON p.id = d.producto_id
                WHERE d.venta_id=?
                ORDER BY d.id
                """;

        List<DetalleVenta> detalles = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto(
                            rs.getLong("p_id"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            desdeCentimos(rs.getLong("precio_centimos")),
                            rs.getInt("stock"),
                            rs.getInt("activo") == 1
                    );

                    detalles.add(
                            new DetalleVenta(
                                    rs.getLong("d_id"),
                                    producto,
                                    rs.getInt("cantidad"),
                                    desdeCentimos(rs.getLong("precio_unitario_centimos"))
                            )
                    );
                }
            }
        }

        return detalles;
    }

    private String consultaBase() {
        return """
                SELECT
                    v.id v_id,
                    v.fecha v_fecha,
                    v.tipo_descuento v_tipo_descuento,
                    v.descuento_centimos v_descuento,
                    v.estado v_estado,
                    u.id u_id,
                    u.nombre u_nombre,
                    u.username u_username,
                    u.password_hash u_password_hash,
                    u.rol u_rol,
                    u.activo u_activo
                FROM venta v
                JOIN usuario u ON u.id = v.vendedor_id
                """;
    }

    private long aCentimos(BigDecimal valor) {
        return valor.movePointRight(2).longValueExact();
    }

    private BigDecimal desdeCentimos(long valor) {
        return BigDecimal.valueOf(valor, 2);
    }
}
