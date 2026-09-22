package pe.edu.unp.ventas.pattern.observer;

import pe.edu.unp.ventas.repository.ComprobanteRepository;

public class ComprobanteVentaObserver
        implements VentaConfirmadaObserver {

    private final ComprobanteRepository repository;

    public ComprobanteVentaObserver(
            ComprobanteRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void actualizar(VentaConfirmadaEvent event) {
        repository.generarComprobanteInterno(
                event.getVenta()
        );
    }
}
