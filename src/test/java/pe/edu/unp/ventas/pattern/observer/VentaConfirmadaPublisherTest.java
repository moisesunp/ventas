package pe.edu.unp.ventas.pattern.observer;

import org.junit.jupiter.api.Test;
import pe.edu.unp.ventas.model.Rol;
import pe.edu.unp.ventas.model.Usuario;
import pe.edu.unp.ventas.model.Venta;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VentaConfirmadaPublisherTest {

    @Test
    void notificaATodosLosObserversRegistrados() {
        Usuario vendedor = new Usuario(
                1L,
                "Vendedor Demo",
                "vendedor",
                "hash",
                Rol.VENDEDOR,
                true
        );

        Venta venta = new Venta(vendedor);

        VentaConfirmadaPublisher publisher =
                new VentaConfirmadaPublisher();

        AtomicInteger llamadas = new AtomicInteger();

        publisher.registrar(
                event -> llamadas.incrementAndGet()
        );
        publisher.registrar(
                event -> llamadas.incrementAndGet()
        );
        publisher.registrar(
                event -> llamadas.incrementAndGet()
        );

        publisher.publicar(venta);

        assertEquals(3, llamadas.get());
    }
}
