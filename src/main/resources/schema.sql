CREATE TABLE IF NOT EXISTS usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    rol TEXT NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'VENDEDOR')),
    activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

CREATE TABLE IF NOT EXISTS producto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    precio_centimos INTEGER NOT NULL CHECK (precio_centimos >= 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

CREATE TABLE IF NOT EXISTS venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    vendedor_id INTEGER NOT NULL,
    subtotal_centimos INTEGER NOT NULL DEFAULT 0 CHECK (subtotal_centimos >= 0),
    tipo_descuento TEXT NOT NULL DEFAULT 'SIN_DESCUENTO'
        CHECK (
            tipo_descuento IN (
                'SIN_DESCUENTO',
                'CLIENTE_FRECUENTE',
                'PROMOCION',
                'EMPLEADO',
                'CAMPANIA_ESPECIAL'
            )
        ),
    descuento_centimos INTEGER NOT NULL DEFAULT 0,
    total_centimos INTEGER NOT NULL DEFAULT 0,
    estado TEXT NOT NULL DEFAULT 'BORRADOR'
        CHECK (estado IN ('BORRADOR', 'CONFIRMADA', 'ANULADA')),

    CONSTRAINT fk_venta_vendedor
        FOREIGN KEY (vendedor_id)
        REFERENCES usuario(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT ck_venta_descuento
        CHECK (
            descuento_centimos >= 0
            AND descuento_centimos <= subtotal_centimos
        ),

    CONSTRAINT ck_venta_total
        CHECK (
            total_centimos >= 0
            AND total_centimos = subtotal_centimos - descuento_centimos
        )
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario_centimos INTEGER NOT NULL CHECK (precio_unitario_centimos >= 0),
    subtotal_centimos INTEGER NOT NULL,

    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (venta_id)
        REFERENCES venta(id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT ck_detalle_subtotal
        CHECK (subtotal_centimos = cantidad * precio_unitario_centimos),

    CONSTRAINT uq_detalle_producto_venta
        UNIQUE (venta_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_usuario_activo ON usuario(activo);
CREATE INDEX IF NOT EXISTS idx_producto_activo ON producto(activo);
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto(nombre);
CREATE INDEX IF NOT EXISTS idx_venta_vendedor ON venta(vendedor_id);
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_venta_estado ON venta(estado);
CREATE INDEX IF NOT EXISTS idx_venta_vendedor_fecha ON venta(vendedor_id, fecha);
CREATE INDEX IF NOT EXISTS idx_detalle_venta ON detalle_venta(venta_id);
CREATE INDEX IF NOT EXISTS idx_detalle_producto ON detalle_venta(producto_id);


CREATE TABLE IF NOT EXISTS auditoria_venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER NOT NULL,
    evento TEXT NOT NULL,
    fecha TEXT NOT NULL,
    vendedor_id INTEGER NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE,
    FOREIGN KEY (vendedor_id) REFERENCES usuario(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS comprobante_venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER NOT NULL UNIQUE,
    numero TEXT NOT NULL UNIQUE,
    fecha TEXT NOT NULL,
    total_centimos INTEGER NOT NULL CHECK (total_centimos >= 0),
    FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notificacion_venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER NOT NULL,
    mensaje TEXT NOT NULL,
    fecha TEXT NOT NULL,
    estado TEXT NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_auditoria_venta ON auditoria_venta(venta_id);
CREATE INDEX IF NOT EXISTS idx_notificacion_venta ON notificacion_venta(venta_id);
