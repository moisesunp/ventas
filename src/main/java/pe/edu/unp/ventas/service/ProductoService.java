package pe.edu.unp.ventas.service;

import pe.edu.unp.ventas.model.Producto;
import pe.edu.unp.ventas.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto registrar(String codigo, String nombre, BigDecimal precio, int stock) {
        if (productoRepository.buscarPorCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("El código del producto ya existe");
        }
        return productoRepository.guardar(new Producto(codigo, nombre, precio, stock));
    }

    public List<Producto> listar() {
        return productoRepository.listar();
    }

    public List<Producto> listarActivos() {
        return productoRepository.listarActivos();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    public void actualizarDatos(Long id, String nombre, BigDecimal precio) {
        Producto producto = buscarPorId(id);
        producto.cambiarNombre(nombre);
        producto.cambiarPrecio(precio);
        productoRepository.actualizar(producto);
    }

    public void aumentarStock(Long id, int cantidad) {
        Producto producto = buscarPorId(id);
        producto.aumentarStock(cantidad);
        productoRepository.actualizar(producto);
    }

    public void activar(Long id) {
        Producto producto = buscarPorId(id);
        producto.activar();
        productoRepository.actualizar(producto);
    }

    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.desactivar();
        productoRepository.actualizar(producto);
    }
}
