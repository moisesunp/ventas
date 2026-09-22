package pe.edu.unp.ventas.pattern.state;

import pe.edu.unp.ventas.model.EstadoVenta;
import pe.edu.unp.ventas.model.Venta;

import java.util.List;

public interface VentaState {
    EstadoVenta getTipo();
    void validarModificable();
    void validarConfirmacion(Venta venta);
    void confirmar(Venta venta);
    void validarAnulacion();
    void anular(Venta venta);
    String descripcion();
    List<String> accionesDisponibles();
}
