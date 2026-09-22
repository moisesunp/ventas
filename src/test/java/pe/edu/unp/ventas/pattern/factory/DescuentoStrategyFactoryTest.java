package pe.edu.unp.ventas.pattern.factory;

import org.junit.jupiter.api.Test;
import pe.edu.unp.ventas.model.TipoDescuento;
import pe.edu.unp.ventas.pattern.strategy.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DescuentoStrategyFactoryTest {

    private final DescuentoStrategyFactory factory =
            new DescuentoStrategyFactory();

    @Test
    void creaLaEstrategiaCorrespondiente() {
        assertInstanceOf(
                SinDescuentoStrategy.class,
                factory.crear(TipoDescuento.SIN_DESCUENTO)
        );
        assertInstanceOf(
                ClienteFrecuenteStrategy.class,
                factory.crear(TipoDescuento.CLIENTE_FRECUENTE)
        );
        assertInstanceOf(
                PromocionStrategy.class,
                factory.crear(TipoDescuento.PROMOCION)
        );
        assertInstanceOf(
                EmpleadoStrategy.class,
                factory.crear(TipoDescuento.EMPLEADO)
        );
        assertInstanceOf(
                CampaniaEspecialStrategy.class,
                factory.crear(TipoDescuento.CAMPANIA_ESPECIAL)
        );
    }
}
