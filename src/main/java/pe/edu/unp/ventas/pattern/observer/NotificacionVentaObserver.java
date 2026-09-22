package pe.edu.unp.ventas.pattern.observer;

import pe.edu.unp.ventas.repository.NotificacionVentaRepository;

public class NotificacionVentaObserver
        implements VentaConfirmadaObserver {

    private final NotificacionVentaRepository repository;

    public NotificacionVentaObserver(
            NotificacionVentaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void actualizar(VentaConfirmadaEvent event) {
        repository.registrarConfirmacion(
                event.getVenta()
        );
    }
}
