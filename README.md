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

## v0.4 — Strategy

Esta versión refactoriza el cálculo de descuentos utilizando el patrón **Strategy**.

### Problema observado en v0.3

```text
VentaService
   │
   ├── calcula 0 %
   ├── calcula 5 %
   ├── calcula 10 %
   ├── calcula 15 %
   └── calcula 20 %
```

`VentaService` conocía directamente todos los algoritmos de descuento.

### Solución en v0.4

Se incorpora:

```text
DescuentoStrategy
      ▲
      │
 ┌────┼───────────────┬───────────────┬───────────────┬──────────────────┐
 │    │               │               │               │
Sin   Cliente         Promocion       Empleado        CampaniaEspecial
Desc. Frecuente       Strategy        Strategy        Strategy
```

Cada estrategia conoce únicamente su propio algoritmo.

`VentaService` ahora hace:

```text
TipoDescuento
     ↓
seleccionar estrategia
     ↓
estrategia.calcular(subtotal)
     ↓
Venta.aplicarDescuento(...)
```

### Qué mejora

- el cálculo de cada descuento está encapsulado;
- cada algoritmo puede cambiar independientemente;
- `VentaService` deja de contener porcentajes;
- las estrategias comparten el contrato `DescuentoStrategy`;
- el comportamiento variable queda representado mediante polimorfismo.

### Qué problema permanece

`VentaService` todavía decide qué implementación crear:

```java
if (tipo == SIN_DESCUENTO) {
    return new SinDescuentoStrategy();
} else if (tipo == CLIENTE_FRECUENTE) {
    return new ClienteFrecuenteStrategy();
} else if (...) {
    ...
}
```

Por tanto, al agregar una nueva estrategia todavía debemos modificar `VentaService`.

Ese será el problema que resolveremos en **v0.5 con Factory**.

## Comparación didáctica

### v0.3

```text
VentaService
   ↓
if / else
   ↓
calcula directamente el descuento
```

### v0.4

```text
VentaService
   ↓
selecciona
   ↓
DescuentoStrategy
   ↓
calcula el descuento
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
- **v0.4: Strategy;**
- v0.5: Factory;
- v0.6: confirmación con responsabilidades crecientes;
- v0.7: Observer;
- v0.8: lógica creciente según estado;
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
