# Ventas

Aplicación académica de escritorio para estudiar la evolución de un sistema pequeño de ventas mediante patrones de diseño.

## Tecnologías

- Java 21
- Maven
- JavaFX
- SQLite

## Versiones disponibles

Las versiones docentes se conservan en ramas estables para que puedan consultarse y descargarse aunque `main` continúe evolucionando.

| Versión | Contenido | Código | Descarga ZIP |
|---|---|---|---|
| v0.1 | Usuarios y productos | [version/v0.1](https://github.com/moisesunp/ventas/tree/version/v0.1) | [Descargar v0.1](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.1.zip) |
| v0.2 | Usuarios, productos y ventas | [version/v0.2](https://github.com/moisesunp/ventas/tree/version/v0.2) | [Descargar v0.2](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.2.zip) |
| v0.3 | Descuentos con condicionales | [version/v0.3](https://github.com/moisesunp/ventas/tree/version/v0.3) | [Descargar v0.3](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.3.zip) |
| v0.4 | Strategy para descuentos | [version/v0.4](https://github.com/moisesunp/ventas/tree/version/v0.4) | [Descargar v0.4](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.4.zip) |
| v0.5 | Factory para creación de estrategias | [version/v0.5](https://github.com/moisesunp/ventas/tree/version/v0.5) | [Descargar v0.5](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.5.zip) |

## v0.5 — Factory

Esta versión mantiene Strategy y añade una fábrica para centralizar la creación de las estrategias concretas de descuento.

### Problema observado en v0.4

```text
VentaService
   │
   ├── new SinDescuentoStrategy()
   ├── new ClienteFrecuenteStrategy()
   ├── new PromocionStrategy()
   ├── new EmpleadoStrategy()
   └── new CampaniaEspecialStrategy()
```

El algoritmo ya estaba encapsulado con Strategy, pero `VentaService` todavía conocía las clases concretas y decidía cuál instanciar.

### Solución en v0.5

Se incorpora:

```text
DescuentoStrategyFactory
          │
          ├── SIN_DESCUENTO
          ├── CLIENTE_FRECUENTE
          ├── PROMOCION
          ├── EMPLEADO
          └── CAMPANIA_ESPECIAL
          │
          ▼
   DescuentoStrategy
```

Ahora `VentaService` hace:

```text
TipoDescuento
     ↓
DescuentoStrategyFactory.crear(tipo)
     ↓
DescuentoStrategy
     ↓
calcular(subtotal)
```

### Qué mejora

- `VentaService` ya no instancia estrategias concretas;
- la decisión de creación queda centralizada;
- Strategy sigue encapsulando el algoritmo;
- Factory encapsula la creación;
- las responsabilidades quedan más separadas.

### Comparación didáctica

#### v0.4

```text
VentaService
   ↓
if / else
   ↓
new EstrategiaConcreta()
   ↓
calcular()
```

#### v0.5

```text
VentaService
   ↓
Factory
   ↓
DescuentoStrategy
   ↓
calcular()
```

## Acceso inicial

```text
usuario: admin
contraseña: admin123
```

## Ejecutar

```bash
mvn clean javafx:run
```

## Evolución didáctica

- v0.1: usuarios y productos;
- v0.2: venta y detalle de venta;
- v0.3: descuentos con condicionales;
- v0.4: Strategy;
- **v0.5: Factory;**
- v0.6: confirmación con responsabilidades crecientes;
- v0.7: Observer;
- v0.8: lógica creciente según estado;
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
