package pe.edu.unp.ventas.pattern.state;

import pe.edu.unp.ventas.model.EstadoVenta;
import pe.edu.unp.ventas.model.Venta;

import java.util.List;

public class ConfirmadaState implements VentaState {

    @Override
    public EstadoVenta getTipo() {
        return EstadoVenta.CONFIRMADA;
    }

    @Override
    public void validarModificable() {
        throw new IllegalStateException(
                "Una venta confirmada ya no puede modificarse"
        );
    }

    @Override
    public void validarConfirmacion(Venta venta) {
        throw new IllegalStateException(
                "La venta ya se encuentra confirmada"
        );
    }

    @Override
    public void confirmar(Venta venta) {
        validarConfirmacion(venta);
    }

    @Override
    public void validarAnulacion() {
        // La venta confirmada sí puede anularse.
    }

    @Override
    public void anular(Venta venta) {
        venta.cambiarEstado(new AnuladaState());
    }

    @Override
    public String descripcion() {
        return "CONFIRMADA: ya no puede editarse, pero puede anularse.";
    }

    @Override
    public List<String> accionesDisponibles() {
        return List.of(
                "CONSULTAR",
                "ANULAR"
        );
    }
}
