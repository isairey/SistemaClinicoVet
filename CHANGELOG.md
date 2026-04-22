# 📝 Registro de Cambios / Changelog

Todos los cambios notables de este proyecto se documentan aquí.
All notable changes to this project are documented here.

El formato está basado en / Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [1.0.0] — 2025

### 🇪🇸 Español

#### Agregado
- **Autenticación** con BCrypt y sistema de login/logout.
- **Recuperación de contraseña** vía email con código OTP (15 min expiración).
- **Gestión de Clientes** — CRUD completo con tipo de persona, contacto de emergencia.
- **Gestión de Mascotas** — Registro con chip, especie, raza, sexo, vinculación a cliente.
- **Historial Clínico** — Observaciones y servicios asociados por mascota.
- **Inventario Multi-Categoría:**
  - Medicamentos (lote, vencimiento, fraccionable, dosis).
  - Alimentos (lote, vencimiento, fraccionable).
  - Material Quirúrgico (lote, vencimiento, fraccionable).
  - Accesorios / Juguetes.
- **Catálogo de Marcas** — CRUD de marcas con descripción.
- **Gestión de Proveedores** — CRUD con NIT/RUT, tipo persona, ciudad.
- **Punto de Venta (POS)** — Ventas con líneas de detalle (productos + servicios).
- **Reportes de Ventas** — Generación por período con total acumulado.
- **Sistema de Alertas** — Notificaciones de vencimiento y stock bajo.
- **Gestión de Usuarios** — Roles y permisos modulares (tabla usuario_modulo).
- **25+ vistas FXML** con diseño profesional en JavaFX.
- **HikariCP** como connection pool para PostgreSQL.
- **Flyway** para migraciones de base de datos.

#### Seguridad
- Todas las credenciales migradas a variables de entorno (`System.getenv()`).
- Contraseñas de usuario hasheadas con BCrypt.
- Tokens OTP con expiración de 15 minutos.

---

### 🇺🇸 English

#### Added
- **Authentication** with BCrypt and login/logout system.
- **Password recovery** via email with OTP code (15 min expiration).
- **Client Management** — Full CRUD with person type, emergency contact.
- **Pet Management** — Registration with chip, species, breed, sex, linked to client.
- **Clinical History** — Observations and associated services per pet.
- **Multi-Category Inventory:**
  - Medications (batch, expiration, fractional, dosage).
  - Food (batch, expiration, fractional).
  - Surgical Materials (batch, expiration, fractional).
  - Accessories / Toys.
- **Brand Catalog** — Brand CRUD with description.
- **Supplier Management** — CRUD with NIT/RUT, person type, city.
- **Point of Sale (POS)** — Sales with detail lines (products + services).
- **Sales Reports** — Generation by period with accumulated total.
- **Alert System** — Expiration and low-stock notifications.
- **User Management** — Modular roles and permissions (usuario_modulo table).
- **25+ FXML views** with professional JavaFX design.
- **HikariCP** as connection pool for PostgreSQL.
- **Flyway** for database migrations.

#### Security
- All credentials migrated to environment variables (`System.getenv()`).
- User passwords hashed with BCrypt.
- OTP tokens with 15-minute expiration.
