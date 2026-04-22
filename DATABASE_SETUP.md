# 🗄️ Guía de Configuración de Base de Datos / Database Setup Guide

---

# 🇪🇸 Español

## Requisitos Previos

| Requisito | Versión Mínima | Enlace |
|---|---|---|
| **PostgreSQL** | 15+ | [postgresql.org/download](https://www.postgresql.org/download/) |
| **Cliente SQL** (opcional) | — | [pgAdmin 4](https://www.pgadmin.org/) / [DBeaver](https://dbeaver.io/) |

> **Nota:** Este proyecto usa PostgreSQL exclusivamente. No es compatible con MySQL, SQLite u otros motores.

## Paso 1: Crear la Base de Datos y el Schema

Si aún no tienes una base de datos PostgreSQL local, créala primero.

### Opción A: Usando la terminal (`psql`)

```bash
# Conectarse como superusuario
psql -U postgres

# Crear la base de datos (si no existe)
CREATE DATABASE postgres;

# Conectar a la base de datos
\c postgres

# El schema se crea automáticamente al ejecutar el script init_schema.sql
```

### Opción B: Usando pgAdmin 4

1. Abre **pgAdmin 4** y conéctate a tu servidor local.
2. Haz clic derecho en **Databases** → **Create** → **Database**.
3. Nombre: `postgres` (o el nombre que prefieras).
4. Haz clic en **Save**.

### Opción C: Usando DBeaver

1. Abre **DBeaver** y crea una nueva conexión PostgreSQL.
2. Host: `localhost`, Puerto: `5432`, Usuario: tu usuario, Contraseña: tu contraseña.
3. Haz clic en **Test Connection** para verificar.
4. Haz clic en **Finish**.

## Paso 2: Ejecutar el Script de Inicialización

El archivo `sql/init_schema.sql` contiene todo el DDL necesario para crear las tablas. El archivo `sql/seed_data.sql` contiene datos de prueba reales (usuarios, clientes, mascotas, productos, ventas, etc.) para que puedas probar todas las funcionalidades inmediatamente.

### Opción A: Usando la terminal (`psql`)

```bash
# Desde la raíz del proyecto

# 1. Crear las tablas (DDL)
psql -U tu_usuario -d postgres -f sql/init_schema.sql

# 2. Cargar datos de prueba (opcional pero recomendado)
psql -U tu_usuario -d postgres -f sql/seed_data.sql
```

### Opción B: Usando pgAdmin 4

1. Abre **pgAdmin 4** y conéctate a tu base de datos.
2. Haz clic en **Tools** → **Query Tool**.
3. Haz clic en el ícono de **carpeta** (Open File) y selecciona `sql/init_schema.sql`.
4. Haz clic en el botón **▶ Execute/Run** (o presiona `F5`).
5. Verifica que no haya errores en la pestaña **Messages**.

### Opción C: Usando DBeaver

1. Abre **DBeaver** y conéctate a tu base de datos.
2. Haz clic en **SQL Editor** → **Open SQL Script**.
3. Selecciona el archivo `sql/init_schema.sql`.
4. Haz clic en **Execute SQL Script** (ícono ▶ naranja, o `Ctrl+Enter`).
5. Verifica los resultados en la pestaña inferior.

## Paso 3: Configurar Variables de Entorno

Después de crear la base de datos, configura las credenciales en tu entorno local.

### Crear archivo `.env`

```bash
# Copiar la plantilla
cp .env.example .env
```

Edita el archivo `.env` con tus credenciales:

```env
# Base de Datos
DB_JDBC_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria
DB_USER=tu_usuario_postgres
DB_PASS=tu_contraseña_postgres

# Email (opcional, para recuperación de contraseñas)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
EMAIL_FROM=tucorreo@gmail.com
EMAIL_PASS=tu_app_password_16_caracteres
EMAIL_FROM_NAME=SOS Veterinaria
```

### Alternativa: Variables de entorno del sistema

**Windows (PowerShell):**
```powershell
$env:DB_JDBC_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria"
$env:DB_USER="postgres"
$env:DB_PASS="tu_contraseña"
```

**Linux / macOS:**
```bash
export DB_JDBC_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria"
export DB_USER="postgres"
export DB_PASS="tu_contraseña"
```

## Paso 4: Verificar la Conexión

Ejecuta el proyecto para verificar que la conexión es exitosa:

```bash
./mvnw clean javafx:run
```

Si la conexión falla, verifica:

1. ✅ PostgreSQL está ejecutándose (`pg_isready` en la terminal).
2. ✅ El schema `veterinaria` fue creado (revisa en pgAdmin → Schemas).
3. ✅ Las variables de entorno están correctamente configuradas.
4. ✅ El puerto `5432` no está bloqueado por un firewall.

## Estructura de la Base de Datos

El script `sql/init_schema.sql` crea las siguientes tablas:

| Tabla | Descripción |
|---|---|
| `usuario` | Usuarios del sistema (autenticación + datos personales) |
| `modulo` | Módulos funcionales del sistema (catálogo) |
| `usuario_modulo` | Relación N:N entre usuarios y módulos (permisos) |
| `password_reset_token` | Tokens OTP para recuperación de contraseña |
| `cliente` | Clientes de la veterinaria |
| `mascota` | Mascotas vinculadas a clientes |
| `historialclinica` | Historial clínico por mascota |
| `servicio` | Catálogo de servicios veterinarios |
| `historialclinica_has_servicio` | Relación N:N historial ↔ servicio |
| `marca` | Marcas de productos |
| `unidadmedida` | Unidades de medida (mg, g, Kg, ml, etc.) |
| `tipoproducto` | Tipos de producto (Medicamento, Alimento, etc.) |
| `proveedor` | Proveedores de productos |
| `producto` | Productos (Single Table Inheritance para subtipos) |
| `permiso` | Permisos del sistema (legacy, basado en arrays de texto) |
| `usuariopermiso` | Relación N:N usuario ↔ permiso (legacy) |
| `proveedor_has_producto` | Relación N:N proveedor ↔ producto |
| `venta` | Encabezado de ventas |
| `lineaventa` | Detalle de ventas (productos + servicios) |

---
---

# 🇺🇸 English

## Prerequisites

| Requirement | Minimum Version | Link |
|---|---|---|
| **PostgreSQL** | 15+ | [postgresql.org/download](https://www.postgresql.org/download/) |
| **SQL Client** (optional) | — | [pgAdmin 4](https://www.pgadmin.org/) / [DBeaver](https://dbeaver.io/) |

> **Note:** This project uses PostgreSQL exclusively. It is not compatible with MySQL, SQLite, or other engines.

## Step 1: Create the Database and Schema

If you don't have a local PostgreSQL database yet, create one first.

### Option A: Using the terminal (`psql`)

```bash
# Connect as superuser
psql -U postgres

# Create the database (if it doesn't exist)
CREATE DATABASE postgres;

# Connect to the database
\c postgres

# The schema is created automatically when running init_schema.sql
```

### Option B: Using pgAdmin 4

1. Open **pgAdmin 4** and connect to your local server.
2. Right-click on **Databases** → **Create** → **Database**.
3. Name: `postgres` (or your preferred name).
4. Click **Save**.

### Option C: Using DBeaver

1. Open **DBeaver** and create a new PostgreSQL connection.
2. Host: `localhost`, Port: `5432`, User: your user, Password: your password.
3. Click **Test Connection** to verify.
4. Click **Finish**.

## Step 2: Run the Initialization Script

The file `sql/init_schema.sql` contains all the DDL needed to create tables. The file `sql/seed_data.sql` contains real test data (users, clients, pets, products, sales, etc.) so you can test all features immediately.

### Option A: Using the terminal (`psql`)

```bash
# From the project root

# 1. Create tables (DDL)
psql -U your_user -d postgres -f sql/init_schema.sql

# 2. Load test data (optional but recommended)
psql -U your_user -d postgres -f sql/seed_data.sql
```

### Option B: Using pgAdmin 4

1. Open **pgAdmin 4** and connect to your database.
2. Click **Tools** → **Query Tool**.
3. Click the **folder** icon (Open File) and select `sql/init_schema.sql`.
4. Click the **▶ Execute/Run** button (or press `F5`).
5. Verify there are no errors in the **Messages** tab.

### Option C: Using DBeaver

1. Open **DBeaver** and connect to your database.
2. Click **SQL Editor** → **Open SQL Script**.
3. Select the file `sql/init_schema.sql`.
4. Click **Execute SQL Script** (orange ▶ icon, or `Ctrl+Enter`).
5. Verify the results in the bottom tab.

## Step 3: Configure Environment Variables

After creating the database, configure the credentials in your local environment.

### Create `.env` file

```bash
# Copy the template
cp .env.example .env
```

Edit the `.env` file with your credentials:

```env
# Database
DB_JDBC_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria
DB_USER=your_postgres_user
DB_PASS=your_postgres_password

# Email (optional, for password recovery)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
EMAIL_FROM=youremail@gmail.com
EMAIL_PASS=your_app_password_16_chars
EMAIL_FROM_NAME=SOS Veterinaria
```

### Alternative: System environment variables

**Windows (PowerShell):**
```powershell
$env:DB_JDBC_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria"
$env:DB_USER="postgres"
$env:DB_PASS="your_password"
```

**Linux / macOS:**
```bash
export DB_JDBC_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria"
export DB_USER="postgres"
export DB_PASS="your_password"
```

## Step 4: Verify the Connection

Run the project to verify the connection is successful:

```bash
./mvnw clean javafx:run
```

If the connection fails, verify:

1. ✅ PostgreSQL is running (`pg_isready` in terminal).
2. ✅ The `veterinaria` schema was created (check in pgAdmin → Schemas).
3. ✅ Environment variables are correctly configured.
4. ✅ Port `5432` is not blocked by a firewall.

## Database Structure

The script `sql/init_schema.sql` creates the following tables:

| Table | Description |
|---|---|
| `usuario` | System users (authentication + personal data) |
| `modulo` | Functional system modules (catalog) |
| `usuario_modulo` | N:N relationship between users and modules (permissions) |
| `password_reset_token` | OTP tokens for password recovery |
| `cliente` | Veterinary clinic clients |
| `mascota` | Pets linked to clients |
| `historialclinica` | Clinical history per pet |
| `servicio` | Veterinary services catalog |
| `historialclinica_has_servicio` | N:N relationship history ↔ service |
| `marca` | Product brands |
| `unidadmedida` | Measurement units (mg, g, Kg, ml, etc.) |
| `tipoproducto` | Product types (Medication, Food, etc.) |
| `proveedor` | Product suppliers |
| `producto` | Products (Single Table Inheritance for subtypes) |
| `permiso` | System permissions (legacy, text array-based) |
| `usuariopermiso` | N:N relationship user ↔ permission (legacy) |
| `proveedor_has_producto` | N:N relationship supplier ↔ product |
| `venta` | Sales header |
| `lineaventa` | Sales detail (products + services) |
