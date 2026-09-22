package pe.edu.unp.ventas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pe.edu.unp.ventas.database.DatabaseManager;
import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.repository.ProductoRepository;
import pe.edu.unp.ventas.repository.UsuarioRepository;
import pe.edu.unp.ventas.repository.VentaRepository;
import pe.edu.unp.ventas.repository.sqlite.SqliteProductoRepository;
import pe.edu.unp.ventas.repository.sqlite.SqliteUsuarioRepository;
import pe.edu.unp.ventas.repository.sqlite.SqliteVentaRepository;
import pe.edu.unp.ventas.service.*;
import pe.edu.unp.ventas.ui.ProductoPane;
import pe.edu.unp.ventas.ui.UsuarioPane;
import pe.edu.unp.ventas.ui.VentaPane;

public class VentasApplication extends Application {
    private AuthService authService;
    private UsuarioService usuarioService;
    private ProductoService productoService;
    private VentaService ventaService;

    @Override
    public void start(Stage stage) {
        configurarAplicacion();
        mostrarLogin(stage);
    }

    private void configurarAplicacion() {
        DatabaseManager database = new DatabaseManager("ventas.db");
        database.inicializarBaseDatos();

        UsuarioRepository usuarioRepository = new SqliteUsuarioRepository(database);
        ProductoRepository productoRepository = new SqliteProductoRepository(database);
        VentaRepository ventaRepository = new SqliteVentaRepository(database);
        PasswordHasher passwordHasher = new PasswordHasher();

        usuarioService = new UsuarioService(usuarioRepository, passwordHasher);
        productoService = new ProductoService(productoRepository);
        ventaService = new VentaService(ventaRepository, productoRepository);
        authService = new AuthService(usuarioRepository, passwordHasher);

        new BootstrapService(usuarioRepository, usuarioService).crearAdministradorInicial();
    }

    private void mostrarLogin(Stage stage) {
        TextField username = new TextField();
        username.setPromptText("Usuario");
        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");
        Label mensaje = new Label();
        Button ingresar = new Button("Ingresar");

        ingresar.setOnAction(e -> {
            try {
                Usuario usuario = authService.autenticar(username.getText(), password.getText());
                mostrarPrincipal(stage, usuario);
            } catch (RuntimeException ex) {
                mensaje.setText(ex.getMessage());
            }
        });

        VBox root = new VBox(10,
                new Label("Sistema académico de ventas - v0.4"),
                username,
                password,
                ingresar,
                mensaje,
                new Label("Acceso inicial: admin / admin123")
        );
        root.setPadding(new Insets(24));
        stage.setTitle("Ventas - Iniciar sesión");
        stage.setScene(new Scene(root, 360, 260));
        stage.show();
    }

    private void mostrarPrincipal(Stage stage, Usuario usuario) {
        TabPane tabs = new TabPane();

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            tabs.getTabs().add(new Tab("Usuarios", new UsuarioPane(usuarioService)));
            tabs.getTabs().add(new Tab("Productos", new ProductoPane(productoService, true)));
        }

        if (usuario.getRol() == Rol.VENDEDOR) {
            tabs.getTabs().add(new Tab("Ventas", new VentaPane(ventaService, productoService, usuario)));
            tabs.getTabs().add(new Tab("Productos", new ProductoPane(productoService, false)));
        }

        tabs.getTabs().forEach(tab -> tab.setClosable(false));

        Button cerrarSesion = new Button("Cerrar sesión");
        cerrarSesion.setOnAction(e -> mostrarLogin(stage));

        VBox root = new VBox(
                8,
                new Label("Sesión: " + usuario.getNombre() + " (" + usuario.getRol() + ")"),
                cerrarSesion,
                tabs
        );
        root.setPadding(new Insets(10));
        VBox.setVgrow(tabs, javafx.scene.layout.Priority.ALWAYS);

        stage.setTitle("Ventas - v0.4");
        stage.setScene(new Scene(root, 1000, 650));
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
