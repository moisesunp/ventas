package pe.edu.unp.ventas.repository.sqlite;

import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Venta;
import pe.edu.unp.ventas.repository.ComprobanteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SqliteComprobanteRepository implements ComprobanteRepository {
    private final DatabaseManager database;

    public SqliteComprobanteRepository(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public String generarComprobanteInterno(Venta venta) {
        if (venta.getId() == null) {
            throw new IllegalArgumentException(
                    "La venta debe estar persistida antes de generar el comprobante"
            );
        }

        String numero = "INT-" + String.format("%06d", venta.getId());

        String sql = """
                INSERT INTO comprobante_venta(
                    venta_id,
                    numero,
                    fecha,
                    total_centimos
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, venta.getId());
            ps.setString(2, numero);
            ps.setString(3, LocalDateTime.now().toString());
            ps.setLong(
                    4,
                    venta.getTotal().movePointRight(2).longValueExact()
            );
            ps.executeUpdate();
            return numero;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo generar el comprobante interno",
                    e
            );
        }
    }
}
