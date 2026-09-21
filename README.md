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

## v0.3 — Descuentos con condicionales

Esta versión incorpora varios tipos de descuento, pero todavía **no utiliza Strategy**. El objetivo académico es hacer visible el crecimiento de la lógica condicional dentro de `VentaService`.

### Tipos de descuento

- `SIN_DESCUENTO`: 0 %
- `CLIENTE_FRECUENTE`: 5 %
- `PROMOCION`: 10 %
- `EMPLEADO`: 15 %
- `CAMPANIA_ESPECIAL`: 20 %

### Qué cambia respecto de v0.2

- se agrega `TipoDescuento`;
- `Venta` conserva el tipo de descuento aplicado;
- `VentaService` decide el porcentaje mediante una cadena `if / else if`;
- el descuento se recalcula cuando cambia el contenido de la venta;
- SQLite persiste `tipo_descuento`;
- existe una migración automática para bases creadas con v0.2;
- JavaFX reemplaza el campo manual de porcentaje por un selector de tipo.

### Problema didáctico visible

```text
VentaService
   │
   ├── if SIN_DESCUENTO
   ├── else if CLIENTE_FRECUENTE
   ├── else if PROMOCION
   ├── else if EMPLEADO
   └── else if CAMPANIA_ESPECIAL
```

La implementación funciona, pero cada nuevo tipo obliga a modificar `VentaService`. Este será el problema que resolveremos en v0.4 mediante Strategy.

### Acceso inicial

```text
usuario: admin
contraseña: admin123
```

Para probar descuentos:
1. ingresar como administrador;
2. registrar productos con stock;
3. crear un usuario con rol `VENDEDOR`;
4. cerrar sesión e ingresar con ese vendedor;
5. crear una venta;
6. seleccionar un tipo de descuento;
7. aplicar el descuento;
8. confirmar la venta.

### Ejecutar

```bash
mvn clean javafx:run
```

## Evolución didáctica prevista

- v0.1: usuarios y productos;
- v0.2: venta y detalle de venta;
- **v0.3: descuentos con condicionales;**
- v0.4: Strategy;
- v0.5: Factory;
- v0.6: confirmación con responsabilidades crecientes;
- v0.7: Observer;
- v0.8: lógica creciente según estado;
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
