package pe.edu.unp.ventas.model;

import pe.edu.unp.ventas.pattern.state.VentaState;
import pe.edu.unp.ventas.pattern.state.VentaStateFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venta {
    private Long id;
    private final LocalDateTime fecha;
    private final Usuario vendedor;
    private final List<DetalleVenta> detalles;
    private TipoDescuento tipoDescuento;
    private BigDecimal descuento;
    private VentaState estado;

    public Venta(Usuario vendedor) {
        this(
                null,
                LocalDateTime.now(),
                vendedor,
                new ArrayList<>(),
                TipoDescuento.SIN_DESCUENTO,
                BigDecimal.ZERO,
                EstadoVenta.BORRADOR
        );
    }

    public Venta(
            Long id,
            LocalDateTime fecha,
            Usuario vendedor,
            List<DetalleVenta> detalles,
            TipoDescuento tipoDescuento,
            BigDecimal descuento,
            EstadoVenta estado
    ) {
        this.id = id;
        this.fecha = Objects.requireNonNull(
                fecha,
                "La fecha es obligatoria"
        );
        this.vendedor = Objects.requireNonNull(
                vendedor,
                "El vendedor es obligatorio"
        );
        this.detalles = new ArrayList<>(
                Objects.requireNonNull(
                        detalles,
                        "Los detalles son obligatorios"
                )
        );
        this.tipoDescuento = Objects.requireNonNull(
                tipoDescuento,
                "El tipo de descuento es obligatorio"
        );
        this.descuento = normalizarMonto(descuento);
        this.estado = VentaStateFactory.desde(estado);
        validarDescuento();
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getVendedor() { return vendedor; }
    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }
    public TipoDescuento getTipoDescuento() {
        return tipoDescuento;
    }
    public EstadoVenta getEstado() {
        return estado.getTipo();
    }
    public String getDescripcionEstado() {
        return estado.descripcion();
    }
    public List<String> getAccionesDisponibles() {
        return estado.accionesDisponibles();
    }

    public BigDecimal getSubtotal() {
        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getTotal() {
        return getSubtotal()
                .subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException(
                    "La venta ya tiene un id asignado"
            );
        }
        this.id = Objects.requireNonNull(id);
    }

    public void cambiarEstado(VentaState nuevoEstado) {
        this.estado = Objects.requireNonNull(
                nuevoEstado,
                "El nuevo estado es obligatorio"
        );
    }

    public void validarModificable() {
        estado.validarModificable();
    }

    public void validarConfirmacion() {
        estado.validarConfirmacion(this);
    }

    public void validarAnulacion() {
        estado.validarAnulacion();
    }

    public void agregarProducto(
            Producto producto,
            int cantidad
    ) {
        estado.validarModificable();

        Objects.requireNonNull(
                producto,
                "El producto es obligatorio"
        );

        DetalleVenta existente =
                buscarDetalle(producto.getId());

        if (existente == null) {
            detalles.add(
                    new DetalleVenta(
                            producto,
                            cantidad
                    )
            );
        } else {
            existente.aumentarCantidad(cantidad);
        }

        if (descuento.compareTo(getSubtotal()) > 0) {
            tipoDescuento =
                    TipoDescuento.SIN_DESCUENTO;
            descuento =
                    BigDecimal.ZERO.setScale(2);
        }
    }

    public void eliminarProducto(Long productoId) {
        estado.validarModificable();

        detalles.removeIf(
                d -> Objects.equals(
                        d.getProducto().getId(),
                        productoId
                )
        );

        if (descuento.compareTo(getSubtotal()) > 0) {
            tipoDescuento =
                    TipoDescuento.SIN_DESCUENTO;
            descuento =
                    BigDecimal.ZERO.setScale(2);
        }
    }

    public void modificarCantidad(
            Long productoId,
            int cantidad
    ) {
        estado.validarModificable();

        DetalleVenta detalle =
                buscarDetalle(productoId);

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El producto no pertenece a la venta"
            );
        }

        detalle.cambiarCantidad(cantidad);
        validarDescuento();
    }

    public void aplicarDescuento(
            TipoDescuento tipoDescuento,
            BigDecimal montoDescuento
    ) {
        estado.validarModificable();

        this.tipoDescuento =
                Objects.requireNonNull(
                        tipoDescuento,
                        "El tipo de descuento es obligatorio"
                );

        Objects.requireNonNull(
                montoDescuento,
                "El monto de descuento es obligatorio"
        );

        if (montoDescuento.signum() < 0
                || montoDescuento.compareTo(
                        getSubtotal()
                ) > 0) {
            throw new IllegalArgumentException(
                    "El descuento no puede ser negativo ni superar el subtotal"
            );
        }

        descuento =
                montoDescuento.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        validarDescuento();
    }

    public void confirmar() {
        estado.confirmar(this);
    }

    public void anular() {
        estado.anular(this);
    }

    private DetalleVenta buscarDetalle(
            Long productoId
    ) {
        return detalles.stream()
                .filter(
                        d -> Objects.equals(
                                d.getProducto().getId(),
                                productoId
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private void validarDescuento() {
        if (descuento.signum() < 0
                || descuento.compareTo(
                        getSubtotal()
                ) > 0) {
            throw new IllegalArgumentException(
                    "El descuento no puede ser negativo ni superar el subtotal"
            );
        }
    }

    private static BigDecimal normalizarMonto(
            BigDecimal monto
    ) {
        Objects.requireNonNull(
                monto,
                "El monto es obligatorio"
        );

        return monto.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}
