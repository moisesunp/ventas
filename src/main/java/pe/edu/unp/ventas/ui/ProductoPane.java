package pe.edu.unp.ventas.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pe.edu.unp.ventas.model.Producto;
import pe.edu.unp.ventas.service.ProductoService;

import java.math.BigDecimal;

public class ProductoPane extends VBox {
    private final ProductoService productoService;
    private final TableView<Producto> tabla = new TableView<>();
    private final TextField codigo = new TextField();
    private final TextField nombre = new TextField();
    private final TextField precio = new TextField();
    private final TextField stock = new TextField();
    private final Label mensaje = new Label();

    public ProductoPane(ProductoService productoService) {
        this.productoService = productoService;
        setPadding(new Insets(12));
        setSpacing(10);
        getChildren().addAll(crearFormulario(), crearTabla(), mensaje);
        refrescar();
    }

    private GridPane crearFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("Código:"), codigo, new Label("Nombre:"), nombre);
        grid.addRow(1, new Label("Precio:"), precio, new Label("Stock:"), stock);

        Button registrar = new Button("Registrar");
        registrar.setOnAction(e -> registrar());
        Button alternar = new Button("Activar / desactivar seleccionado");
        alternar.setOnAction(e -> alternarActivo());
        grid.addRow(2, registrar, alternar);
        return grid;
    }

    private TableView<Producto> crearTabla() {
        TableColumn<Producto, String> codigoCol = new TableColumn<>("Código");
        codigoCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCodigo()));
        TableColumn<Producto, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<Producto, String> precioCol = new TableColumn<>("Precio");
        precioCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("S/ " + c.getValue().getPrecio()));
        TableColumn<Producto, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStock()));
        TableColumn<Producto, String> activoCol = new TableColumn<>("Activo");
        activoCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isActivo() ? "Sí" : "No"));
        tabla.getColumns().addAll(codigoCol, nombreCol, precioCol, stockCol, activoCol);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return tabla;
    }

    private void registrar() {
        try {
            productoService.registrar(
                    codigo.getText(),
                    nombre.getText(),
                    new BigDecimal(precio.getText()),
                    Integer.parseInt(stock.getText())
            );
            codigo.clear();
            nombre.clear();
            precio.clear();
            stock.clear();
            mensaje.setText("Producto registrado correctamente.");
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText("Datos inválidos: " + ex.getMessage());
        }
    }

    private void alternarActivo() {
        Producto seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje.setText("Seleccione un producto.");
            return;
        }
        if (seleccionado.isActivo()) {
            productoService.desactivar(seleccionado.getId());
        } else {
            productoService.activar(seleccionado.getId());
        }
        refrescar();
    }

    private void refrescar() {
        tabla.setItems(FXCollections.observableArrayList(productoService.listar()));
    }
}
