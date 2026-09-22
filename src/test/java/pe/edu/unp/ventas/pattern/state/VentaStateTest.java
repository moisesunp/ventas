package pe.edu.unp.ventas.pattern.state;

import org.junit.jupiter.api.Test;
import pe.edu.unp.ventas.model.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VentaStateTest {

    private Venta nuevaVentaConProducto() {
        Usuario vendedor = new Usuario(
                1L,
                "Vendedor Demo",
                "vendedor",
                "hash",
                Rol.VENDEDOR,
                true
        );

        Producto producto = new Producto(
                1L,
                "P001",
                "Producto Demo",
                new BigDecimal("100.00"),
                10,
                true
        );

        Venta venta = new Venta(vendedor);
        venta.agregarProducto(producto, 1);
        return venta;
    }

    @Test
    void transicionaDeBorradorAConfirmadaYAnulada() {
        Venta venta = nuevaVentaConProducto();

        assertEquals(EstadoVenta.BORRADOR, venta.getEstado());

        venta.confirmar();
        assertEquals(EstadoVenta.CONFIRMADA, venta.getEstado());

        venta.anular();
        assertEquals(EstadoVenta.ANULADA, venta.getEstado());
    }

    @Test
    void unaVentaConfirmadaNoPuedeModificarse() {
        Venta venta = nuevaVentaConProducto();
        venta.confirmar();

        assertThrows(
                IllegalStateException.class,
                () -> venta.aplicarDescuento(
                        TipoDescuento.PROMOCION,
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void unaVentaAnuladaNoPuedeVolverAAnularse() {
        Venta venta = nuevaVentaConProducto();
        venta.confirmar();
        venta.anular();

        assertThrows(
                IllegalStateException.class,
                venta::anular
        );
    }

    @Test
    void cadaEstadoExponeSusAcciones() {
        Venta venta = nuevaVentaConProducto();

        assertTrue(
                venta.getAccionesDisponibles().contains("CONFIRMAR")
        );

        venta.confirmar();

        assertTrue(
                venta.getAccionesDisponibles().contains("ANULAR")
        );

        venta.anular();

        assertEquals(
                1,
                venta.getAccionesDisponibles().size()
        );
        assertEquals(
                "CONSULTAR",
                venta.getAccionesDisponibles().getFirst()
        );
    }
}
