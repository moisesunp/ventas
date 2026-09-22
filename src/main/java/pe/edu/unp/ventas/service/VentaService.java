package pe.edu.unp.ventas.service;

import pe.edu.unp.ventas.model.*;
import pe.edu.unp.ventas.pattern.strategy.*;
import pe.edu.unp.ventas.repository.ProductoRepository;
import pe.edu.unp.ventas.repository.VentaRepository;

import java.util.List;

public class VentaService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public Venta crearVenta(Usuario vendedor) {
        validarVendedor(vendedor);
        return new Venta(vendedor);
    }

    public void agregarProducto(Venta venta, Long productoId, int cantidad) {
        validarBorrador(venta);

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (!producto.isActivo()) {
            throw new IllegalStateException("El producto está inactivo");
        }

        int cantidadActual = venta.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(productoId))
                .mapToInt(DetalleVenta::getCantidad)
                .sum();

        if (cantidadActual + cantidad > producto.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente");
        }

        venta.agregarProducto(producto, cantidad);
        recalcularDescuento(venta);
    }

    public void eliminarProducto(Venta venta, Long productoId) {
        validarBorrador(venta);
        venta.eliminarProducto(productoId);
        recalcularDescuento(venta);
    }

    public void aplicarDescuento(Venta venta, TipoDescuento tipo) {
        validarBorrador(venta);

        DescuentoStrategy estrategia = seleccionarEstrategia(tipo);
        venta.aplicarDescuento(tipo, estrategia.calcular(venta.getSubtotal()));
    }

    public Venta confirmarVenta(Venta venta) {
        validarVendedor(venta.getVendedor());
        validarBorrador(venta);

        if (venta.getDetalles().isEmpty()) {
            throw new IllegalStateException("La venta debe contener al menos un producto");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto actual = productoRepository.buscarPorId(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (!actual.isActivo()) {
                throw new IllegalStateException("Producto inactivo: " + actual.getNombre());
            }

            if (actual.getStock() < detalle.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente: " + actual.getNombre());
            }
        }

        recalcularDescuento(venta);
        ventaRepository.guardarConfirmada(venta);
        venta.confirmar();
        return venta;
    }

    public Venta anularVenta(Long id) {
        Venta venta = buscarPorId(id);

        if (venta.getEstado() != EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("Solo una venta confirmada puede anularse");
        }

        ventaRepository.anularConfirmada(venta);
        venta.anular();
        return venta;
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
    }

    public List<Venta> listar() {
        return ventaRepository.listar();
    }

    public List<Venta> listarPorVendedor(Long vendedorId) {
        return ventaRepository.listarPorVendedor(vendedorId);
    }

    private void recalcularDescuento(Venta venta) {
        DescuentoStrategy estrategia = seleccionarEstrategia(venta.getTipoDescuento());
        venta.aplicarDescuento(
                venta.getTipoDescuento(),
                estrategia.calcular(venta.getSubtotal())
        );
    }

    private DescuentoStrategy seleccionarEstrategia(TipoDescuento tipo) {
        if (tipo == TipoDescuento.SIN_DESCUENTO) {
            return new SinDescuentoStrategy();
        } else if (tipo == TipoDescuento.CLIENTE_FRECUENTE) {
            return new ClienteFrecuenteStrategy();
        } else if (tipo == TipoDescuento.PROMOCION) {
            return new PromocionStrategy();
        } else if (tipo == TipoDescuento.EMPLEADO) {
            return new EmpleadoStrategy();
        } else if (tipo == TipoDescuento.CAMPANIA_ESPECIAL) {
            return new CampaniaEspecialStrategy();
        } else {
            throw new IllegalArgumentException("Tipo de descuento no reconocido");
        }
    }

    private void validarVendedor(Usuario vendedor) {
        if (vendedor == null || !vendedor.isActivo()) {
            throw new IllegalStateException("El vendedor debe estar activo");
        }

        if (vendedor.getRol() != Rol.VENDEDOR) {
            throw new IllegalStateException("Solo un usuario con rol VENDEDOR puede registrar ventas");
        }
    }

    private void validarBorrador(Venta venta) {
        if (venta == null || venta.getEstado() != EstadoVenta.BORRADOR) {
            throw new IllegalStateException("La venta debe estar en estado BORRADOR");
        }
    }
}
