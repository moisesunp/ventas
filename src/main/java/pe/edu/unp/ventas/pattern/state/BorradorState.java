package pe.edu.unp.ventas.pattern.state;

import pe.edu.unp.ventas.model.EstadoVenta;
import pe.edu.unp.ventas.model.Venta;

import java.util.List;

public class BorradorState implements VentaState {

    @Override
    public EstadoVenta getTipo() {
        return EstadoVenta.BORRADOR;
    }

    @Override
    public void validarModificable() {
        // El borrador sí puede modificarse.
    }

    @Override
    public void validarConfirmacion(Venta venta) {
        if (venta.getDetalles().isEmpty()) {
            throw new IllegalStateException(
                    "La venta debe contener al menos un producto"
            );
        }
    }

    @Override
    public void confirmar(Venta venta) {
        validarConfirmacion(venta);
        venta.cambiarEstado(new ConfirmadaState());
    }

    @Override
    public void validarAnulacion() {
        throw new IllegalStateException(
                "Una venta en borrador todavía no puede anularse desde el historial"
        );
    }

    @Override
    public void anular(Venta venta) {
        validarAnulacion();
    }

    @Override
    public String descripcion() {
        return "BORRADOR: puede modificarse y confirmarse.";
    }

    @Override
    public List<String> accionesDisponibles() {
        return List.of(
                "AGREGAR_PRODUCTO",
                "QUITAR_PRODUCTO",
                "APLICAR_DESCUENTO",
                "CONFIRMAR"
        );
    }
}
