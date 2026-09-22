package pe.edu.unp.ventas.repository;

import pe.edu.unp.ventas.model.Venta;

public interface AuditoriaVentaRepository {
    void registrarConfirmacion(Venta venta);
}
