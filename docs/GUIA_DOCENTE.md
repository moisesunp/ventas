# Guía docente — Sistema de ventas y patrones de diseño

## 1. Propósito

Este proyecto está construido como una secuencia didáctica. La idea no es presentar los patrones de diseño desde el inicio, sino mostrar primero el problema que hace necesario cada patrón.

## 2. Secuencia recomendada

### v0.1 — Base del sistema
Usuarios y productos.

Objetivo: comprender dominio, servicios, repositorios y persistencia.

### v0.2 — Ventas
Se incorporan venta y detalle de venta.

Objetivo: comprender reglas de negocio y coordinación entre capas.

### v0.3 — Problema de descuentos
Los descuentos se resuelven mediante condicionales.

Pregunta para clase:

> ¿Qué ocurre si Marketing agrega diez tipos nuevos de descuento?

### v0.4 — Strategy
Cada algoritmo de descuento se mueve a una estrategia concreta.

Idea central:

```text
comportamiento variable
        ↓
DescuentoStrategy
```

### v0.5 — Factory
La creación de estrategias se centraliza.

Idea central:

```text
VentaService
    ↓
Factory
    ↓
Strategy
```

### v0.6 — Problema de múltiples consecuencias
Confirmar una venta provoca auditoría, comprobante y notificación mediante llamadas directas.

Pregunta para clase:

> ¿Debe VentaService conocer todas las cosas que ocurren después de confirmar una venta?

### v0.7 — Observer
La venta confirmada se convierte en un evento.

```text
VentaService
    ↓
publica evento
    ↓
observers
```

### v0.8 — Problema de estados
La lógica de BORRADOR, CONFIRMADA y ANULADA se dispersa en condicionales.

Pregunta para clase:

> ¿Qué pasa si agregamos PENDIENTE_PAGO, EN_REVISION o DEVUELTA?

### v0.9 — State
Cada estado conoce su propio comportamiento.

```text
Venta
  ↓
VentaState
  ├── BorradorState
  ├── ConfirmadaState
  └── AnuladaState
```

### v1.0 — Integración
Versión final para ejecutar, comparar y estudiar.

## 3. Arquitectura

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

Los patrones se ubican principalmente en:

```text
pattern/
├── strategy/
├── factory/
├── observer/
└── state/
```

## 4. Patrones y responsabilidad

| Patrón | Problema que resuelve | Elemento principal |
|---|---|---|
| Strategy | algoritmos de descuento variables | DescuentoStrategy |
| Factory | creación de estrategias concretas | DescuentoStrategyFactory |
| Observer | múltiples reacciones a una confirmación | VentaConfirmadaPublisher |
| State | comportamiento dependiente del estado | VentaState |

## 5. Flujo completo de una venta

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
Strategy calcula descuento
   ↓
confirma venta
   ↓
State valida transición
   ↓
Repository persiste y descuenta stock
   ↓
Observer publica venta confirmada
   ↓
Auditoría / Comprobante / Notificación
```

## 6. Comparaciones recomendadas en clase

Las comparaciones más útiles son:

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

## 7. Ejecución

```bash
mvn clean javafx:run
```

Acceso inicial:

```text
usuario: admin
contraseña: admin123
```

## 8. Pruebas

```bash
mvn test
```

Las pruebas cubren:

- cálculo de estrategias;
- creación de estrategias mediante Factory;
- publicación a múltiples observers;
- transiciones y restricciones de State.

## 9. Nota pedagógica

El proyecto no pretende afirmar que todos los sistemas de ventas deban implementar exactamente estos patrones. Su objetivo es mostrar cómo una necesidad de diseño puede aparecer progresivamente y cómo un patrón puede responder a esa necesidad.
