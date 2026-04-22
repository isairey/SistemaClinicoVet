-- =====================================================================
-- 🐾 SOS Veterinaria — Datos de Prueba / Sample Seed Data
-- =====================================================================
-- Motor / Engine: PostgreSQL 15+
-- Schema: veterinaria
--
-- ⚠️ Ejecuta PRIMERO init_schema.sql antes de este archivo.
-- ⚠️ Run init_schema.sql FIRST before this file.
--
-- Este script inserta datos reales de prueba para que puedas probar
-- todas las funcionalidades del sistema inmediatamente.
--
-- This script inserts real test data so you can test all system
-- features immediately.
-- =====================================================================

-- =====================================================================
-- CATÁLOGOS / CATALOGS
-- =====================================================================

-- Tipos de producto
INSERT INTO veterinaria.tipoproducto (idtipoproducto, "nombreTipo") VALUES
    (1, 'Medicamento'),
    (2, 'Alimento'),
    (3, 'Material Quirúrgico'),
    (4, 'Accesorio/Juguete')
ON CONFLICT DO NOTHING;

-- Unidades de medida
INSERT INTO veterinaria.unidadmedida (idunidadmedida, nombre) VALUES
    (1,  'Unidad'),
    (2,  'Caja'),
    (3,  'Kilogramo'),
    (4,  'Gramo'),
    (5,  'Tableta'),
    (6,  'Cápsula'),
    (7,  'Frasco'),
    (8,  'Ampolla'),
    (9,  'Paquete'),
    (10, 'Tarro'),
    (11, 'Rollo'),
    (12, 'Bolsa'),
    (13, 'Litro'),
    (14, 'Mililitro')
ON CONFLICT DO NOTHING;

-- Módulos del sistema
INSERT INTO veterinaria.modulo (idmodulo, nombremodulo, descripcion, icono, orden)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'Registrar Productos',     'Permite crear y editar productos en el sistema',            NULL, 1),
    (2, 'Inventario',              'Permite administrar el inventario de productos',             NULL, 2),
    (3, 'Gestionar Usuarios',      'Permite crear, editar y eliminar usuarios del sistema',      NULL, 3),
    (4, 'Visualizar Registros',    'Permite ver historial clínico y registros',                  NULL, 4),
    (5, 'Ventas',                  'Permite realizar ventas y gestionar transacciones',           NULL, 5),
    (6, 'Agregar Clientes',        'Permite registrar y editar clientes',                        NULL, 6),
    (7, 'Administrar Proveedores', 'Permite gestionar proveedores',                              NULL, 7)
ON CONFLICT DO NOTHING;

-- Marcas
INSERT INTO veterinaria.marca (idmarca, nombremarca, descripcion) VALUES
    (1, 'Adidas',    'Marca de ropa'),
    (2, 'Nike',      'Marca deportiva'),
    (4, 'Falabella', 'Marca comercial'),
    (5, 'Abott',     'Gasa que evita los germenes en la piel')
ON CONFLICT DO NOTHING;

-- Servicios
INSERT INTO veterinaria.servicio (idservicio, nombreservicio, descripcion, precio) VALUES
    (8, 'Lavado de mascota', 'Aqui se hara una lavado general a la mascota de manera sencilla', 25000.00)
ON CONFLICT DO NOTHING;

-- Permisos (legacy)
INSERT INTO veterinaria.permiso (idpermiso, modulos, descripcion) VALUES
    (1, '{registro_productos,productos}',                                                    'Registrar Productos'),
    (2, '{inventario,gestionar_inventario,alimentos,medicamentos,juguetes,accesorios}',      'Gestionar Inventario'),
    (3, '{usuarios,gestionar_usuarios,crear_usuarios,modificar_usuarios}',                   'Gestionar Usuarios'),
    (4, '{registros,visualizar_registros,consultas}',                                        'Visualizar Registros'),
    (5, '{ventas,registrar_ventas,punto_venta}',                                             'Ventas'),
    (6, '{proveedores,gestionar_proveedores,agregar_proveedores}',                           'Administrar Proveedores'),
    (7, '{clientes,agregar_clientes,gestionar_clientes}',                                    'Agregar Clientes')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- USUARIOS / USERS
-- =====================================================================
-- ⚠️ Las contraseñas están hasheadas con BCrypt (12 rondas).
-- ⚠️ Passwords are hashed with BCrypt (12 rounds).
--
-- Para pruebas, las contraseñas originales son:
-- For testing, the original passwords are:
--   Usuario "Dabji"  → contraseña: (pregunta al admin / ask admin)
--   Usuario "Yudy"   → contraseña: (pregunta al admin / ask admin)
--   Usuario "David"  → contraseña: (pregunta al admin / ask admin)

INSERT INTO veterinaria.usuario (idusuario, cc, nombre, apellidos, usuario, email, contrasena, telefono, direccion) VALUES
    (2, '1095791730', 'Juan David',     'Ordoñez Ferreira', 'Dabji', 'juanordo1403@gmail.com',           '$2a$12$YsRwWC0LnTU9eCMl3h0rfeKAWfK491ytW9OyBlGcUI5X7f91bzG8y', '3023963757', 'Avenida Siempre Viva'),
    (4, '63334029',   'Maria Judith',   'Ferreira Rojas',   'Yudy',  'mariayudyferreirasena@gmail.com',  '$2a$12$Z56xet82Q3zTp24VLOsSUuXA9smNKPnK0vT40CQZyrLzxiFhItlPe', '3222075100', 'Av.Bellavista #152-47 Conjunto Residencial Panorama'),
    (5, '1098656432', 'David Eduardo',  'Florez Duran',     'David', 'Florezd@gmail.com',                '$2a$12$er0ydCPp0KusFUUA3..jGuChslpp46X5p15ObU9zrbJQrtxhATftm',  '324545232',  'Cra 4 9-29')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PROVEEDORES / SUPPLIERS
-- =====================================================================

INSERT INTO veterinaria.proveedor (idproveedor, nombre, apellido, tipopersona, nit_rut, direccion, telefono, email, ciudad, tipodocumento) VALUES
    (13, 'David eduardo', 'Villamizar Hernandez', 'Natural', '31468532', 'Car 3 9-24', '3176543241', 'davidf@gmail.com', 'Bucaramanga', 'NIT')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- CLIENTES / CLIENTS
-- =====================================================================

INSERT INTO veterinaria.cliente (idcliente, nombre, apellidos, tipopersona, cc, fechanacimiento, email, direccion, telefono, nombrecontactoemergencia, telefonocontactoemergencia, ciudad, tipodocumento) VALUES
    (21, 'David Ricardo', 'Carvajal Barragan', 'Natural', '1097494121', '2006-10-06', 'richardcarvajalb@gmail.com', 'Cra 3 8-18', '3175436114', 'Luis Francisco', 'Carvajal Villamizar', 'Bucaramanga', 'CC')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- MASCOTAS / PETS
-- =====================================================================

INSERT INTO veterinaria.mascota (idmascota, nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente) VALUES
    (27, 'Sparky', 'Perro salchicha',  'Perro', 'M', '31254675432', 2, NULL),
    (28, 'sparky', 'Perro Salchicha',  'Perro', 'M', '12332146323', 2, 21)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PRODUCTOS / PRODUCTS
-- =====================================================================
-- Nota: imagenproducto (BYTEA) se inserta como bytes vacíos para la demo.
-- Para el producto real, la imagen se carga desde la UI.
-- Note: imagenproducto (BYTEA) is inserted as empty bytes for demo.
-- For the real product, the image is loaded from the UI.

INSERT INTO veterinaria.producto (idproducto, nombre, precio, costo, referencia, codigobarras, stock, descripcion, unidadesingresadas, lote, "fechaVencimiento", semanaalerta, fraccionado, fraccionable, contenido, tipoproducto_idtipoproducto, usuario_idusuario, marca_idmarca, unidadmedida_idunidadmedida, imagenproducto, estado, "dosisUnidad") VALUES
    (15, 'Pepas Fruko', 25000.00, 20000.00, 'Megaredil', '31765438976', 3.00, 'Las pepas Fruko vienes 2 paquetes por lote', 5, '475329', '2028-10-14', 2, false, false, 2.00, 1, NULL, 5, 3, E'\\x', 'ACTIVO', NULL)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- VENTAS / SALES
-- =====================================================================

INSERT INTO veterinaria.venta (idventa, fecha, totalventa, usuario_idusuario, cliente_idcliente) VALUES
    (24, '2025-10-28', 75000.00, NULL, 21)
ON CONFLICT DO NOTHING;

-- Líneas de venta / Sale lines
INSERT INTO veterinaria.lineaventa (idlineaventa, cantidad, subtotal, venta_idventa, producto_idproducto, servicio_idservicio, valor) VALUES
    (37, 2, 50000.00, 24, 15,   NULL, 25000),
    (38, 1, 25000.00, 24, NULL, 8,    25000)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PERMISOS DE USUARIO / USER PERMISSIONS (módulos)
-- =====================================================================

-- Usuario David (id=5) tiene acceso a: Registrar Productos, Inventario, Administrar Proveedores
INSERT INTO veterinaria.usuario_modulo (idusuario_modulo, usuario_idusuario, modulo_idmodulo, fecha_asignacion)
OVERRIDING SYSTEM VALUE VALUES
    (92, 5, 1, '2025-10-29 03:59:32.729995'),
    (93, 5, 2, '2025-10-29 03:59:32.729995'),
    (94, 5, 7, '2025-10-29 03:59:32.729995')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PROVEEDOR <-> PRODUCTO / SUPPLIER <-> PRODUCT
-- =====================================================================

INSERT INTO veterinaria.proveedor_has_producto (idproveedor_has_producto, producto_idproducto, proveedor_idproveedor) VALUES
    (9, 15, 13)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- SECUENCIAS: Ajustar valores para evitar conflictos con nuevos INSERTs
-- SEQUENCES: Adjust values to avoid conflicts with new INSERTs
-- =====================================================================

-- Tablas con GENERATED BY DEFAULT necesitan ajuste manual de secuencia
SELECT setval(pg_get_serial_sequence('veterinaria.usuario',               'idusuario'),              COALESCE((SELECT MAX(idusuario)              FROM veterinaria.usuario),              1));
SELECT setval(pg_get_serial_sequence('veterinaria.cliente',               'idcliente'),              COALESCE((SELECT MAX(idcliente)              FROM veterinaria.cliente),              1));
SELECT setval(pg_get_serial_sequence('veterinaria.mascota',               'idmascota'),              COALESCE((SELECT MAX(idmascota)              FROM veterinaria.mascota),              1));
SELECT setval(pg_get_serial_sequence('veterinaria.marca',                 'idmarca'),                COALESCE((SELECT MAX(idmarca)                FROM veterinaria.marca),                1));
SELECT setval(pg_get_serial_sequence('veterinaria.unidadmedida',          'idunidadmedida'),         COALESCE((SELECT MAX(idunidadmedida)         FROM veterinaria.unidadmedida),         1));
SELECT setval(pg_get_serial_sequence('veterinaria.tipoproducto',          'idtipoproducto'),         COALESCE((SELECT MAX(idtipoproducto)         FROM veterinaria.tipoproducto),         1));
SELECT setval(pg_get_serial_sequence('veterinaria.permiso',               'idpermiso'),              COALESCE((SELECT MAX(idpermiso)              FROM veterinaria.permiso),              1));
SELECT setval(pg_get_serial_sequence('veterinaria.proveedor',             'idproveedor'),            COALESCE((SELECT MAX(idproveedor)            FROM veterinaria.proveedor),            1));
SELECT setval(pg_get_serial_sequence('veterinaria.servicio',              'idservicio'),             COALESCE((SELECT MAX(idservicio)             FROM veterinaria.servicio),             1));
SELECT setval(pg_get_serial_sequence('veterinaria.producto',              'idproducto'),             COALESCE((SELECT MAX(idproducto)             FROM veterinaria.producto),             1));
SELECT setval(pg_get_serial_sequence('veterinaria.venta',                 'idventa'),                COALESCE((SELECT MAX(idventa)                FROM veterinaria.venta),                1));
SELECT setval(pg_get_serial_sequence('veterinaria.lineaventa',            'idlineaventa'),           COALESCE((SELECT MAX(idlineaventa)           FROM veterinaria.lineaventa),           1));
SELECT setval(pg_get_serial_sequence('veterinaria.proveedor_has_producto','idproveedor_has_producto'),COALESCE((SELECT MAX(idproveedor_has_producto) FROM veterinaria.proveedor_has_producto),1));
SELECT setval(pg_get_serial_sequence('veterinaria.historialclinica',      'idhistorialclinica'),     COALESCE((SELECT MAX(idhistorialclinica)     FROM veterinaria.historialclinica),     1));

-- Tablas con GENERATED ALWAYS (modulo, password_reset_token, usuario_modulo)
-- no necesitan ajuste manual — PostgreSQL las maneja automáticamente.

-- =====================================================================
-- ✅ Datos cargados — El sistema está listo para probar
-- ✅ Data loaded — The system is ready to test
-- =====================================================================
