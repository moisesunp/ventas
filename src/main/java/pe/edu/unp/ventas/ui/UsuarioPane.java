package pe.edu.unp.ventas.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.service.UsuarioService;

public class UsuarioPane extends VBox {
    private final UsuarioService usuarioService;
    private final TableView<Usuario> tabla = new TableView<>();
    private final TextField nombre = new TextField();
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final ComboBox<Rol> rol = new ComboBox<>(FXCollections.observableArrayList(Rol.values()));
    private final Label mensaje = new Label();

    public UsuarioPane(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        setPadding(new Insets(12));
        setSpacing(10);
        rol.setValue(Rol.VENDEDOR);
        getChildren().addAll(crearFormulario(), crearTabla(), mensaje);
        refrescar();
    }

    private GridPane crearFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("Nombre:"), nombre, new Label("Usuario:"), username);
        grid.addRow(1, new Label("Contraseña:"), password, new Label("Rol:"), rol);

        Button registrar = new Button("Registrar");
        registrar.setOnAction(e -> registrar());
        Button alternar = new Button("Activar / desactivar seleccionado");
        alternar.setOnAction(e -> alternarActivo());
        grid.addRow(2, registrar, alternar);
        return grid;
    }

    private TableView<Usuario> crearTabla() {
        TableColumn<Usuario, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().getId()));
        TableColumn<Usuario, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<Usuario, String> userCol = new TableColumn<>("Usuario");
        userCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        TableColumn<Usuario, String> rolCol = new TableColumn<>("Rol");
        rolCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRol().name()));
        TableColumn<Usuario, String> activoCol = new TableColumn<>("Activo");
        activoCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isActivo() ? "Sí" : "No"));
        tabla.getColumns().addAll(idCol, nombreCol, userCol, rolCol, activoCol);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return tabla;
    }

    private void registrar() {
        try {
            usuarioService.registrar(nombre.getText(), username.getText(), password.getText(), rol.getValue());
            nombre.clear();
            username.clear();
            password.clear();
            mensaje.setText("Usuario registrado correctamente.");
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void alternarActivo() {
        Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje.setText("Seleccione un usuario.");
            return;
        }
        if (seleccionado.isActivo()) {
            usuarioService.desactivar(seleccionado.getId());
        } else {
            usuarioService.activar(seleccionado.getId());
        }
        refrescar();
    }

    private void refrescar() {
        tabla.setItems(FXCollections.observableArrayList(usuarioService.listar()));
    }
}
