package pe.edu.unp.ventas.pattern.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DescuentoStrategyTest {

    private final BigDecimal subtotal = new BigDecimal("200.00");

    @Test
    void calculaDescuentosEsperados() {
        assertEquals(new BigDecimal("0.00"), new SinDescuentoStrategy().calcular(subtotal));
        assertEquals(new BigDecimal("10.00"), new ClienteFrecuenteStrategy().calcular(subtotal));
        assertEquals(new BigDecimal("20.00"), new PromocionStrategy().calcular(subtotal));
        assertEquals(new BigDecimal("30.00"), new EmpleadoStrategy().calcular(subtotal));
        assertEquals(new BigDecimal("40.00"), new CampaniaEspecialStrategy().calcular(subtotal));
    }
}
