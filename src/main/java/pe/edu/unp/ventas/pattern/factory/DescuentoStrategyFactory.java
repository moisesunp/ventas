package pe.edu.unp.ventas.pattern.factory;

import pe.edu.unp.ventas.model.TipoDescuento;
import pe.edu.unp.ventas.pattern.strategy.*;

import java.util.Objects;

public class DescuentoStrategyFactory {

    public DescuentoStrategy crear(TipoDescuento tipo) {
        Objects.requireNonNull(tipo, "El tipo de descuento es obligatorio");

        return switch (tipo) {
            case SIN_DESCUENTO -> new SinDescuentoStrategy();
            case CLIENTE_FRECUENTE -> new ClienteFrecuenteStrategy();
            case PROMOCION -> new PromocionStrategy();
            case EMPLEADO -> new EmpleadoStrategy();
            case CAMPANIA_ESPECIAL -> new CampaniaEspecialStrategy();
        };
    }
}
