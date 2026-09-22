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
| v0.9 | State para comportamiento de la venta | [version/v0.9](https://github.com/moisesunp/ventas/tree/version/v0.9) | [Descargar v0.9](https://github.com/moisesunp/ventas/archive/refs/heads/version/v0.9.zip) |

## v0.9 — State

Esta versión refactoriza el comportamiento dependiente del estado mediante el patrón **State**.

### Problema observado en v0.8

La lógica de estado estaba repartida entre `Venta` y `VentaService`:

```text
if BORRADOR ...
else if CONFIRMADA ...
else if ANULADA ...
```

y aparecía para modificar, confirmar, anular, describir el estado y decidir acciones disponibles.

### Solución en v0.9

Se introduce el contrato:

```text
VentaState
   │
   ├── BorradorState
   ├── ConfirmadaState
   └── AnuladaState
```

Cada estado conoce:

```text
validarModificable()
validarConfirmacion()
confirmar()
validarAnulacion()
anular()
descripcion()
accionesDisponibles()
```

`Venta` mantiene una referencia al estado actual y delega el comportamiento:

```text
Venta
  │
  └── VentaState
          │
          ├── BorradorState
          ├── ConfirmadaState
          └── AnuladaState
```

### Transiciones

```text
BORRADOR
   │ confirmar
   ▼
CONFIRMADA
   │ anular
   ▼
ANULADA
```

`ANULADA` es terminal.

### Persistencia

SQLite continúa almacenando:

```text
BORRADOR
CONFIRMADA
ANULADA
```

Cuando una venta se recupera desde la base de datos, `VentaStateFactory` reconstruye el objeto de estado correspondiente.

De esta forma, la persistencia sigue siendo sencilla mientras el dominio trabaja con objetos State.

### Comparación didáctica

#### v0.8

```text
Venta / VentaService
      ↓
if estado == ...
      ↓
decidir comportamiento
```

#### v0.9

```text
Venta
  ↓
estado.actualizar comportamiento
  ↓
BorradorState / ConfirmadaState / AnuladaState
```

### Qué mejora

- desaparecen los grandes bloques condicionales por estado del servicio;
- cada estado concentra sus propias reglas;
- las transiciones quedan explícitas;
- agregar un nuevo estado requiere una nueva implementación de `VentaState`;
- la interfaz puede consultar descripción y acciones desde el propio estado.

## Patrones incorporados hasta v0.9

```text
Strategy  → algoritmo variable de descuento
Factory   → creación de estrategias
Observer  → reacciones a venta confirmada
State     → comportamiento según estado
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
- v0.8: lógica creciente según estado;
- **v0.9: State;**
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
