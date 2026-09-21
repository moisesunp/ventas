package pe.edu.unp.ventas.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DetalleVenta {
    private Long id;
    private final Producto producto;
    private int cantidad;
    private final BigDecimal precioUnitario;

    public DetalleVenta(Producto producto, int cantidad) {
        this(null, producto, cantidad, producto.getPrecio());
    }

    public DetalleVenta(Long id, Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.producto = Objects.requireNonNull(producto, "El producto es obligatorio");
        validarCantidad(cantidad);
        this.cantidad = cantidad;
        Objects.requireNonNull(precioUnitario, "El precio unitario es obligatorio");
        if (precioUnitario.signum() < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El detalle ya tiene un id asignado");
        }
        this.id = Objects.requireNonNull(id);
    }

    public void cambiarCantidad(int cantidad) {
        validarCantidad(cantidad);
        this.cantidad = cantidad;
    }

    public void aumentarCantidad(int cantidadAdicional) {
        if (cantidadAdicional <= 0) {
            throw new IllegalArgumentException("La cantidad adicional debe ser mayor que cero");
        }
        this.cantidad += cantidadAdicional;
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }
}
