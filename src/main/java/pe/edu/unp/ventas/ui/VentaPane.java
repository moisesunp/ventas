package pe.edu.unp.ventas.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import pe.edu.unp.ventas.model.DetalleVenta;
import pe.edu.unp.ventas.model.Producto;
import pe.edu.unp.ventas.model.TipoDescuento;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.model.Venta;
import pe.edu.unp.ventas.service.ProductoService;
import pe.edu.unp.ventas.service.VentaService;

public class VentaPane extends VBox {
    private final VentaService ventaService;
    private final ProductoService productoService;
    private final Usuario vendedor;

    private Venta ventaActual;
    private final ComboBox<Producto> producto = new ComboBox<>();
    private final Spinner<Integer> cantidad = new Spinner<>(1, 999, 1);
    private final ComboBox<TipoDescuento> tipoDescuento =
            new ComboBox<>(FXCollections.observableArrayList(TipoDescuento.values()));
    private final TableView<DetalleVenta> tabla = new TableView<>();
    private final Label subtotal = new Label();
    private final Label descuentoMonto = new Label();
    private final Label total = new Label();
    private final Label mensaje = new Label();

    public VentaPane(VentaService ventaService, ProductoService productoService, Usuario vendedor) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.vendedor = vendedor;

        setPadding(new Insets(12));
        setSpacing(10);
        configurarProductoCombo();
        configurarTabla();
        tipoDescuento.setValue(TipoDescuento.SIN_DESCUENTO);

        getChildren().addAll(
                new Label("Nueva venta"),
                crearControles(),
                tabla,
                crearTotales(),
                crearAcciones(),
                mensaje
        );

        nuevaVenta();
    }

    private void configurarProductoCombo() {
        producto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto p) {
                if (p == null) return "";
                return p.getCodigo() + " - " + p.getNombre() + " (stock: " + p.getStock() + ")";
            }

            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    private void configurarTabla() {
        TableColumn<DetalleVenta, String> productoCol = new TableColumn<>("Producto");
        productoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProducto().getNombre()));

        TableColumn<DetalleVenta, Number> cantidadCol = new TableColumn<>("Cantidad");
        cantidadCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidad()));

        TableColumn<DetalleVenta, String> precioCol = new TableColumn<>("Precio");
        precioCol.setCellValueFactory(c -> new SimpleStringProperty("S/ " + c.getValue().getPrecioUnitario()));

        TableColumn<DetalleVenta, String> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(c -> new SimpleStringProperty("S/ " + c.getValue().getSubtotal()));

        tabla.getColumns().addAll(productoCol, cantidadCol, precioCol, subtotalCol);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPrefHeight(330);
    }

    private GridPane crearControles() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        producto.setPrefWidth(360);
        tipoDescuento.setPrefWidth(220);

        Button agregar = new Button("Agregar");
        agregar.setOnAction(e -> agregarProducto());

        Button quitar = new Button("Quitar seleccionado");
        quitar.setOnAction(e -> quitarProducto());

        Button aplicar = new Button("Aplicar descuento");
        aplicar.setOnAction(e -> aplicarDescuento());

        grid.addRow(0, new Label("Producto:"), producto, new Label("Cantidad:"), cantidad, agregar);
        grid.addRow(1, new Label("Tipo descuento:"), tipoDescuento, aplicar, quitar);

        return grid;
    }

    private HBox crearTotales() {
        return new HBox(
                25,
                new Label("Subtotal:"), subtotal,
                new Label("Descuento:"), descuentoMonto,
                new Label("Total:"), total
        );
    }

    private HBox crearAcciones() {
        Button confirmar = new Button("Confirmar venta");
        confirmar.setOnAction(e -> confirmarVenta());

        Button nueva = new Button("Nueva venta");
        nueva.setOnAction(e -> nuevaVenta());

        return new HBox(10, confirmar, nueva);
    }

    private void nuevaVenta() {
        try {
            ventaActual = ventaService.crearVenta(vendedor);
            producto.setItems(FXCollections.observableArrayList(productoService.listarActivos()));

            if (!producto.getItems().isEmpty()) {
                producto.getSelectionModel().selectFirst();
            }

            cantidad.getValueFactory().setValue(1);
            tipoDescuento.setValue(TipoDescuento.SIN_DESCUENTO);
            mensaje.setText("");
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void agregarProducto() {
        Producto seleccionado = producto.getValue();

        if (seleccionado == null) {
            mensaje.setText("Seleccione un producto.");
            return;
        }

        try {
            ventaService.agregarProducto(
                    ventaActual,
                    seleccionado.getId(),
                    cantidad.getValue()
            );
            mensaje.setText("Producto agregado.");
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void quitarProducto() {
        DetalleVenta seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mensaje.setText("Seleccione un detalle.");
            return;
        }

        try {
            ventaService.eliminarProducto(
                    ventaActual,
                    seleccionado.getProducto().getId()
            );
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void aplicarDescuento() {
        try {
            ventaService.aplicarDescuento(
                    ventaActual,
                    tipoDescuento.getValue()
            );
            mensaje.setText("Descuento " + ventaActual.getTipoDescuento() + " aplicado.");
            refrescar();
        } catch (RuntimeException ex) {
            mensaje.setText("Descuento inválido: " + ex.getMessage());
        }
    }

    private void confirmarVenta() {
        try {
            ventaService.confirmarVenta(ventaActual);
            mensaje.setText(
                    "Venta N.° " + ventaActual.getId()
                            + " confirmada con "
                            + ventaActual.getTipoDescuento()
                            + "."
            );
            refrescar();
            producto.setItems(FXCollections.observableArrayList(productoService.listarActivos()));
        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void refrescar() {
        tabla.setItems(FXCollections.observableArrayList(ventaActual.getDetalles()));
        subtotal.setText("S/ " + ventaActual.getSubtotal());
        descuentoMonto.setText("S/ " + ventaActual.getDescuento());
        total.setText("S/ " + ventaActual.getTotal());
    }
}
