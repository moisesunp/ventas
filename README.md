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
| v0.6 | Confirmación con consecuencias directas | [version/v0.6](https://github.com/moisesunp/ventas/tree/version/v0.6) | [Descargar v0.6](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.6.zip) |
| v0.7 | Observer para venta confirmada | [version/v0.7](https://github.com/moisesunp/ventas/tree/version/v0.7) | [Descargar v0.7](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.7.zip) |
| v0.8 | Lógica condicional por estado | [version/v0.8](https://github.com/moisesunp/ventas/tree/version/v0.8) | [Descargar v0.8](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.8.zip) |

## v0.8 — Problema previo a State

Esta versión hace visible el crecimiento de las reglas que dependen del estado de la venta, pero todavía **no utiliza State**.

### Estados actuales

```text
BORRADOR
CONFIRMADA
ANULADA
```

### Qué se añadió

- historial de ventas del vendedor;
- posibilidad de anular una venta confirmada;
- descripción distinta según el estado;
- listado de acciones permitidas según el estado.

### Lógica condicional creciente

Ahora distintas operaciones deben preguntar repetidamente:

```java
if (estado == BORRADOR) {
    ...
} else if (estado == CONFIRMADA) {
    ...
} else if (estado == ANULADA) {
    ...
}
```

Por ejemplo, `VentaService` decide:

```text
descripcionEstado()
accionesDisponibles()
anularVenta()
validarBorrador()
```

mientras `Venta` también contiene comprobaciones de estado para:

```text
agregar producto
eliminar producto
modificar cantidad
aplicar descuento
confirmar
anular
```

### Problema didáctico visible

La lógica asociada a un mismo estado empieza a quedar repartida por distintos métodos y clases.

```text
BORRADOR
   ├── puede editar
   ├── puede aplicar descuento
   └── puede confirmar

CONFIRMADA
   ├── no puede editar
   ├── puede consultarse
   └── puede anularse

ANULADA
   ├── no puede editar
   ├── no puede confirmarse
   └── solo puede consultarse
```

Si aparece un nuevo estado, por ejemplo:

```text
PENDIENTE_PAGO
EN_REVISION
DEVUELTA
```

habría que localizar y modificar numerosos condicionales.

Ese será el problema que resolveremos en **v0.9 con State**.

## Comparación esperada

### v0.8

```text
Venta / VentaService
      ↓
if estado == ...
      ↓
decidir comportamiento
```

### v0.9

```text
Venta
  ↓
VentaState
  │
  ├── BorradorState
  ├── ConfirmadaState
  └── AnuladaState
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
- v0.5: Factory;
- v0.6: confirmación con responsabilidades crecientes;
- v0.7: Observer;
- **v0.8: lógica creciente según estado;**
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
