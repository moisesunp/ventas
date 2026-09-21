# Ventas

Aplicación académica de escritorio para estudiar la evolución de un sistema pequeño de ventas mediante patrones de diseño.

## v0.1 — Usuarios y productos

Esta primera versión evita deliberadamente los patrones GoF. Su finalidad es establecer una base funcional y comprensible antes de que aparezcan problemas de diseño que justifiquen las refactorizaciones posteriores.

### Tecnologías

- Java 21
- Maven
- JavaFX
- SQLite

### Funcionalidad actual

- autenticación de usuarios;
- registro y listado de usuarios;
- activación y desactivación de usuarios;
- roles `ADMINISTRADOR` y `VENDEDOR`;
- registro y listado de productos;
- activación y desactivación de productos;
- precio monetario persistido en céntimos;
- stock no negativo;
- persistencia SQLite mediante Repository.

### Ejecutar

```bash
mvn clean javafx:run
```

En la primera ejecución se crea automáticamente un usuario académico inicial:

```text
usuario: admin
contraseña: admin123
```

La base local `ventas.db` se crea en el directorio desde el que se ejecuta la aplicación y no se versiona.

## Estructura

```text
src/main/java/pe/edu/unp/ventas/
├── database/
├── model/
├── repository/
│   └── sqlite/
├── service/
└── ui/
```

El flujo general es:

```text
JavaFX → Service → Repository → SQLite
             ↓
           Modelo
```

## Evolución didáctica prevista

- v0.1: usuarios y productos;
- v0.2: venta y detalle de venta;
- v0.3: crecimiento deliberado de condicionales;
- v0.4: Strategy;
- v0.5: Factory;
- v0.7: Observer;
- v0.9: State;
- v1.0: integración académica final.

La regla del proyecto es introducir cada patrón **después de que el problema sea visible**, no antes.
