package pe.edu.unp.ventas.pattern.strategy;

import pe.edu.unp.ventas.model.TipoDescuento;

import java.math.BigDecimal;

public interface DescuentoStrategy {
    TipoDescuento getTipo();
    BigDecimal calcular(BigDecimal subtotal);
}
