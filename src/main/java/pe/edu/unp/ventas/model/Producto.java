package pe.edu.unp.ventas.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Producto {
    private Long id;
    private final String codigo;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private boolean activo;

    public Producto(Long id, String codigo, String nombre, BigDecimal precio, int stock, boolean activo) {
        this.id = id;
        this.codigo = validarTexto(codigo, "código");
        this.nombre = validarTexto(nombre, "nombre");
        cambiarPrecio(precio);
        validarStock(stock);
        this.stock = stock;
        this.activo = activo;
    }

    public Producto(String codigo, String nombre, BigDecimal precio, int stock) {
        this(null, codigo, nombre, precio, stock, true);
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public boolean isActivo() { return activo; }

    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El producto ya tiene un id asignado");
        }
        this.id = Objects.requireNonNull(id);
    }

    public void cambiarNombre(String nombre) {
        this.nombre = validarTexto(nombre, "nombre");
    }

    public void cambiarPrecio(BigDecimal precio) {
        Objects.requireNonNull(precio, "El precio es obligatorio");
        if (precio.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio.setScale(2);
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        stock += cantidad;
    }

    public void disminuirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (cantidad > stock) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        stock -= cantidad;
    }

    public void activar() { activo = true; }
    public void desactivar() { activo = false; }

    private static void validarStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " es obligatorio");
        }
        return valor.trim();
    }
}
