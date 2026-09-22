package pe.edu.unp.ventas.pattern.strategy;

import pe.edu.unp.ventas.model.TipoDescuento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SinDescuentoStrategy implements DescuentoStrategy {
    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.SIN_DESCUENTO;
    }

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
