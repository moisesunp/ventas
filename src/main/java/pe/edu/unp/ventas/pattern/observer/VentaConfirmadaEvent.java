package pe.edu.unp.ventas.pattern.observer;

import pe.edu.unp.ventas.model.Venta;

import java.util.Objects;

public class VentaConfirmadaEvent {
    private final Venta venta;

    public VentaConfirmadaEvent(Venta venta) {
        this.venta = Objects.requireNonNull(
                venta,
                "La venta es obligatoria"
        );
    }

    public Venta getVenta() {
        return venta;
    }
}
