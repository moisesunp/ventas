# Ventas

Aplicación académica de escritorio para estudiar la evolución de un sistema pequeño de ventas mediante patrones de diseño.

## Tecnologías

- Java 21
- Maven
- JavaFX
- SQLite
- JUnit 5

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
| v1.0 | Integración académica final | [version/v1.0](https://github.com/moisesunp/ventas/tree/version/v1.0) | [Descargar v1.0](https://github.com/moisesunp/ventas/archive/refs/heads/version/v1.0.zip) |

## v1.0 — Integración académica final

Esta versión reúne el sistema completo y los cuatro patrones trabajados durante la evolución del proyecto.

### Arquitectura general

```text
JavaFX
   ↓
Service
   ↓
Domain Model
   ↓
Repository
   ↓
SQLite
```

Los patrones se encuentran en:

```text
pattern/
├── strategy/
├── factory/
├── observer/
└── state/
```

### Patrones integrados

| Patrón | Problema que resuelve |
|---|---|
| Strategy | algoritmos variables de descuento |
| Factory | creación de estrategias concretas |
| Observer | múltiples reacciones a una venta confirmada |
| State | comportamiento que cambia según el estado de la venta |

### Flujo de una venta

```text
Vendedor
   ↓
crea venta
   ↓
agrega productos
   ↓
selecciona descuento
   ↓
Factory crea Strategy
   ↓
Strategy calcula
   ↓
State valida la confirmación
   ↓
Repository persiste y actualiza stock
   ↓
Observer publica venta confirmada
   ↓
Auditoría / Comprobante / Notificación
```

### Comparaciones didácticas principales

```text
v0.3 → v0.4
condicionales → Strategy

v0.4 → v0.5
creación dentro del servicio → Factory

v0.6 → v0.7
llamadas directas → Observer

v0.8 → v0.9
condicionales por estado → State
```

### Pruebas

La versión 1.0 incorpora pruebas de:

- Strategy;
- Factory;
- Observer;
- State.

Ejecutar:

```bash
mvn test
```

### Ejecutar la aplicación

```bash
mvn clean javafx:run
```

Acceso inicial:

```text
usuario: admin
contraseña: admin123
```

## Guía docente

La secuencia completa para explicar el proyecto en clase está en:

[docs/GUIA_DOCENTE.md](docs/GUIA_DOCENTE.md)

## Evolución didáctica completa

```text
v0.1  Base: usuarios y productos
v0.2  Ventas
v0.3  Problema de descuentos
v0.4  Strategy
v0.5  Factory
v0.6  Problema de múltiples consecuencias
v0.7  Observer
v0.8  Problema de estados
v0.9  State
v1.0  Integración académica final
```

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
