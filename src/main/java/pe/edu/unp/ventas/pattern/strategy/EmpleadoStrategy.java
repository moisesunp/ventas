package pe.edu.unp.ventas.pattern.strategy;

import pe.edu.unp.ventas.model.TipoDescuento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EmpleadoStrategy implements DescuentoStrategy {
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.15");

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.EMPLEADO;
    }

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return subtotal.multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
    }
}
