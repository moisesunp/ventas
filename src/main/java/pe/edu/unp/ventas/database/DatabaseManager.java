package pe.edu.unp.ventas.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String url;

    public DatabaseManager(String archivo) {
        this.url = "jdbc:sqlite:" + archivo;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    public void inicializarBaseDatos() {
        String script = cargarSchema();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            for (String sentencia : script.split(";")) {
                String sql = sentencia.trim();

                if (!sql.isEmpty()) {
                    statement.execute(sql);
                }
            }

            migrarAV03(connection);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo inicializar la base de datos",
                    e
            );
        }
    }

    private void migrarAV03(Connection connection) throws SQLException {
        if (!existeColumna(connection, "venta", "tipo_descuento")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        ALTER TABLE venta
                        ADD COLUMN tipo_descuento TEXT NOT NULL
                        DEFAULT 'SIN_DESCUENTO'
                        """);
            }
        }
    }

    private boolean existeColumna(
            Connection connection,
            String tabla,
            String columna
    ) throws SQLException {

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "PRAGMA table_info(" + tabla + ")"
             )) {

            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private String cargarSchema() {
        try (InputStream input =
                     DatabaseManager.class.getResourceAsStream("/schema.sql")) {

            if (input == null) {
                throw new IllegalStateException(
                        "No se encontró /schema.sql"
                );
            }

            return new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer /schema.sql",
                    e
            );
        }
    }
}
