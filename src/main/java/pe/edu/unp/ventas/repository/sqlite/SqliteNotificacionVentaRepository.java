package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Venta;
import pe.edu.unp.ventas.repository.NotificacionVentaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SqliteNotificacionVentaRepository implements NotificacionVentaRepository {
    private final DatabaseManager database;

    public SqliteNotificacionVentaRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public void registrarConfirmacion(
            Venta venta,
            String numeroComprobante
    ) {
        String mensaje =
                "Venta " + venta.getId()
                        + " confirmada. Comprobante interno: "
                        + numeroComprobante;

        String sql = """
                INSERT INTO notificacion_venta(
                    venta_id,
                    mensaje,
                    fecha,
                    estado
                )
                VALUES (?, ?, ?, 'REGISTRADA')
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, venta.getId());
            ps.setString(2, mensaje);
            ps.setString(3, LocalDateTime.now().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo registrar la notificación de la venta",
                    e
            );
        }
    }
}
