package pe.edu.unp.ventas.repository;

import pe.edu.unp.ventas.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto guardar(Producto producto);
    void actualizar(Producto producto);
    Optional<Producto> buscarPorId(Long id);
    Optional<Producto> buscarPorCodigo(String codigo);
    List<Producto> listar();
    List<Producto> listarActivos();
}
