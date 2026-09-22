package pe.edu.unp.ventas.pattern.observer;

import pe.edu.unp.ventas.repository.AuditoriaVentaRepository;

public class AuditoriaVentaObserver
        implements VentaConfirmadaObserver {

    private final AuditoriaVentaRepository repository;

    public AuditoriaVentaObserver(
            AuditoriaVentaRepository repository
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
