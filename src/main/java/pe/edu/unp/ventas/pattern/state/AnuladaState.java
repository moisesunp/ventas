package pe.edu.unp.ventas.pattern.state;

import pe.edu.unp.ventas.model.EstadoVenta;
import pe.edu.unp.ventas.model.Venta;

import java.util.List;

public class AnuladaState implements VentaState {

    @Override
    public EstadoVenta getTipo() {
        return EstadoVenta.ANULADA;
    }

    @Override
    public void validarModificable() {
        throw new IllegalStateException(
                "Una venta anulada no puede modificarse"
        );
    }

    @Override
    public void validarConfirmacion(Venta venta) {
        throw new IllegalStateException(
                "Una venta anulada no puede confirmarse"
        );
    }

    @Override
    public void confirmar(Venta venta) {
        validarConfirmacion(venta);
    }

    @Override
    public void validarAnulacion() {
        throw new IllegalStateException(
                "La venta ya se encuentra anulada"
        );
    }

    @Override
    public void anular(Venta venta) {
        validarAnulacion();
    }

    @Override
    public String descripcion() {
        return "ANULADA: es un estado final y no admite nuevas operaciones.";
    }

    @Override
    public List<String> accionesDisponibles() {
        return List.of("CONSULTAR");
    }
}
