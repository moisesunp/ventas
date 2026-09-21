package pe.edu.unp.ventas.repository;

import pe.edu.unp.ventas.model.Venta;

import java.util.List;
import java.util.Optional;

public interface VentaRepository {
    Venta guardarConfirmada(Venta venta);
    void anularConfirmada(Venta venta);
    Optional<Venta> buscarPorId(Long id);
    List<Venta> listar();
    List<Venta> listarPorVendedor(Long vendedorId);
}
