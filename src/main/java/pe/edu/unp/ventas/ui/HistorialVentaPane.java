package pe.edu.unp.ventas.ui;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.model.Venta;
import pe.edu.unp.ventas.service.VentaService;

public class HistorialVentaPane extends VBox {
    private final VentaService ventaService;
    private final Usuario vendedor;
    private final TableView<Venta> tabla = new TableView<>();
    private final Label estadoDetalle = new Label();
    private final Label acciones = new Label();
    private final Label mensaje = new Label();

    public HistorialVentaPane(
            VentaService ventaService,
            Usuario vendedor
    ) {
        this.ventaService = ventaService;
        this.vendedor = vendedor;

        setPadding(new Insets(12));
        setSpacing(10);

        configurarTabla();

        Button actualizar = new Button("Actualizar");
        actualizar.setOnAction(e -> refrescar());

        Button anular = new Button("Anular seleccionada");
        anular.setOnAction(e -> anularSeleccionada());

        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, actual) ->
                        mostrarEstado(actual)
                );

        getChildren().addAll(
                new Label("Historial de ventas"),
                tabla,
                new HBox(10, actualizar, anular),
                estadoDetalle,
                acciones,
                mensaje
        );

        refrescar();
    }

    private void configurarTabla() {
        TableColumn<Venta, Number> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(
                c -> new SimpleLongProperty(c.getValue().getId())
        );

        TableColumn<Venta, String> fechaCol =
                new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getFecha().toString()
                )
        );

        TableColumn<Venta, String> descuentoCol =
                new TableColumn<>("Descuento");
        descuentoCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getTipoDescuento().name()
                )
        );

        TableColumn<Venta, String> totalCol =
                new TableColumn<>("Total");
        totalCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        "S/ " + c.getValue().getTotal()
                )
        );

        TableColumn<Venta, String> estadoCol =
                new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getEstado().name()
                )
        );

        tabla.getColumns().addAll(
                idCol,
                fechaCol,
                descuentoCol,
                totalCol,
                estadoCol
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        tabla.setPrefHeight(420);
    }

    private void anularSeleccionada() {
        Venta seleccionada =
                tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensaje.setText("Seleccione una venta.");
            return;
        }

        try {
            Venta anulada =
                    ventaService.anularVenta(seleccionada.getId());

            mensaje.setText(
                    "Venta N.° " + anulada.getId()
                            + " anulada correctamente."
            );

            refrescar();

        } catch (RuntimeException ex) {
            mensaje.setText(ex.getMessage());
        }
    }

    private void mostrarEstado(Venta venta) {
        if (venta == null) {
            estadoDetalle.setText("");
            acciones.setText("");
            return;
        }

        estadoDetalle.setText(
                ventaService.descripcionEstado(venta)
        );

        acciones.setText(
                "Acciones disponibles: "
                        + String.join(
                                ", ",
                                ventaService.accionesDisponibles(venta)
                        )
        );
    }

    private void refrescar() {
        tabla.setItems(
                FXCollections.observableArrayList(
                        ventaService.listarPorVendedor(
                                vendedor.getId()
                        )
                )
        );

        Venta seleccionada =
                tabla.getSelectionModel().getSelectedItem();

        mostrarEstado(seleccionada);
    }
}
