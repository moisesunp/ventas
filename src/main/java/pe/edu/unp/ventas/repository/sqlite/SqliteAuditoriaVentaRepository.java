package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Venta;
import pe.edu.unp.ventas.repository.AuditoriaVentaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SqliteAuditoriaVentaRepository implements AuditoriaVentaRepository {
    private final DatabaseManager database;

    public SqliteAuditoriaVentaRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public void registrarConfirmacion(Venta venta) {
        String sql = """
                INSERT INTO auditoria_venta(
                    venta_id,
                    evento,
                    fecha,
                    vendedor_id
                )
                VALUES (?, 'VENTA_CONFIRMADA', ?, ?)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, venta.getId());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setLong(3, venta.getVendedor().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo registrar la auditoría de la venta",
                    e
            );
        }
    }
}
