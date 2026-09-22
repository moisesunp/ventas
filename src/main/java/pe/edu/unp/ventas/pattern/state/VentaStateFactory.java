package pe.edu.unp.ventas.pattern.state;

import pe.edu.unp.ventas.model.EstadoVenta;

import java.util.Objects;

public final class VentaStateFactory {
    private VentaStateFactory() {
    }

    public static VentaState desde(EstadoVenta estado) {
        Objects.requireNonNull(
                estado,
                "El estado de venta es obligatorio"
        );

        return switch (estado) {
            case BORRADOR -> new BorradorState();
            case CONFIRMADA -> new ConfirmadaState();
            case ANULADA -> new AnuladaState();
        };
    }
}
