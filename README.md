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

Commits de referencia:

- v0.1: `9221847ce6dfb7d1f52d21d3834ee6da6e77f56a`
- v0.2: `027d0af1dca6a1869dd300df77c21f1dbcd825bc`

> Nota: estas ramas cumplen por ahora la función de versiones congeladas. Cuando el repositorio tenga tags/releases de GitHub, se mantendrán apuntando a estos mismos commits.

## v0.2 — Usuarios, productos y ventas

Esta versión incorpora el flujo comercial básico, pero todavía **no utiliza patrones GoF**. El objetivo académico es disponer primero de una implementación convencional y funcional, de modo que los problemas de crecimiento del código puedan observarse antes de introducir Strategy, Factory, Observer y State.

### Funcionalidad actual

**Administrador**
- iniciar sesión;
- registrar usuarios;
- activar y desactivar usuarios;
- crear usuarios con rol `VENDEDOR`;
- registrar productos;
- consultar productos;
- activar y desactivar productos.

**Vendedor**
- iniciar sesión;
- consultar productos activos;
- crear una venta;
- agregar y quitar productos;
- aplicar un descuento porcentual simple;
- consultar subtotal, descuento y total;
- confirmar una venta.

### Reglas principales

- una venta nueva comienza en `BORRADOR`;
- solo un usuario activo con rol `VENDEDOR` puede registrar ventas;
- solo productos activos pueden agregarse;
- la cantidad debe ser mayor que cero;
- debe existir stock suficiente;
- un producto aparece una sola vez por venta;
- el precio unitario queda congelado en el detalle;
- el descuento no puede superar el subtotal;
- confirmar la venta descuenta stock;
- venta, detalles y actualización de stock se ejecutan dentro de una transacción SQLite;
- una venta confirmada no puede modificarse;
- el modelo ya contempla `ANULADA` y reposición de stock desde el servicio/repositorio.

### Acceso inicial

En la primera ejecución se crea automáticamente:

```text
usuario: admin
contraseña: admin123
```

Para probar ventas:
1. ingresar como administrador;
2. registrar al menos un producto con stock;
3. registrar un usuario con rol `VENDEDOR`;
4. cerrar sesión;
5. ingresar con el vendedor;
6. abrir la pestaña **Ventas**.

### Ejecutar

```bash
mvn clean javafx:run
```

La base local `ventas.db` se crea en el directorio desde el que se ejecuta la aplicación y no se versiona.

## Estructura

```text
src/main/java/pe/edu/unp/ventas/
├── database/
├── model/
│   ├── Usuario
│   ├── Producto
│   ├── Venta
│   ├── DetalleVenta
│   ├── Rol
│   └── EstadoVenta
├── repository/
│   └── sqlite/
├── service/
└── ui/
```

El flujo general sigue siendo:

```text
JavaFX → Service → Repository → SQLite
             ↓
           Modelo
```

## Evolución didáctica prevista

- v0.1: usuarios y productos;
- **v0.2: venta y detalle de venta;**
- v0.3: crecimiento deliberado de condicionales;
- v0.4: Strategy;
- v0.5: Factory;
- v0.6: confirmación con responsabilidades crecientes;
- v0.7: Observer;
- v0.8: lógica creciente según estado;
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
