package pe.edu.unp.ventas.pattern.observer;

import pe.edu.unp.ventas.model.Venta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VentaConfirmadaPublisher {
    private final List<VentaConfirmadaObserver> observers =
            new ArrayList<>();

    public void registrar(VentaConfirmadaObserver observer) {
        observers.add(
                Objects.requireNonNull(
                        observer,
                        "El observer es obligatorio"
                )
        );
    }

    public void remover(VentaConfirmadaObserver observer) {
        observers.remove(observer);
    }

    public void publicar(Venta venta) {
        VentaConfirmadaEvent event =
                new VentaConfirmadaEvent(venta);

        for (VentaConfirmadaObserver observer : observers) {
            observer.actualizar(event);
        }
    }
}
