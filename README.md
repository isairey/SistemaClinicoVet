<p align="center">
  <img src="src/main/resources/co/edu/upb/veterinaria/images/logoPrincipalPrincipal.png" alt="SOS Veterinaria Logo" width="200"/>
</p>

<h1 align="center">🐾 Veterinaria SOS — Enterprise Management System</h1>

<p align="center">
  <strong>Sistema integral de gestión para clínicas veterinarias</strong><br/>
  <em>Comprehensive management system for veterinary clinics</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-23-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 23"/>
  <img src="https://img.shields.io/badge/JavaFX-17-007396?style=for-the-badge&logo=java&logoColor=white" alt="JavaFX 17"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/HikariCP-5.0-00B4D8?style=for-the-badge" alt="HikariCP"/>
  <img src="https://img.shields.io/badge/BCrypt-0.10-6C3483?style=for-the-badge" alt="BCrypt"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License"/>
  <img src="https://img.shields.io/badge/Grade-5.0%2F5.0-brightgreen?style=for-the-badge" alt="Grade 5.0"/>
</p>

<p align="center">
  <a href="#-español">Español</a> · <a href="#-english">English</a>
</p>

---

<p align="center">
  <img src="demo.gif" alt="Veterinaria SOS Demo" width="800"/>
  <br/>
  <em>📹 Demo completa del sistema / Full system demo</em>
</p>

---

# 🇪🇸 Español

## Acerca del Proyecto

**Veterinaria SOS** es un sistema de gestión empresarial (ERP) diseñado para clínicas veterinarias. Fue desarrollado como MVP académico para la materia de **Ingeniería de Software** (4to semestre) en la **Universidad Pontificia Bolivariana (UPB)**, obteniendo una calificación de **5.0/5.0**.

El sistema permite administrar clientes, mascotas, inventario (medicamentos, alimentos, accesorios, material quirúrgico), proveedores, ventas (POS), historial clínico, usuarios con permisos modulares y recuperación de contraseña por email con código OTP.

## Galería de Interfaces (UI Showcase)

<table>
  <tr>
    <td align="center" width="50%">
      <img src="docs/assets/01_login_view.png" alt="Login" width="100%"/>
      <br/><strong>Inicio de Sesión</strong>
    </td>
    <td align="center" width="50%">
      <img src="docs/assets/02_password_recovery.png" alt="Password Recovery" width="100%"/>
      <br/><strong>Recuperación de Contraseña (OTP)</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/04_main_dashboard.png" alt="Dashboard" width="100%"/>
      <br/><strong>Panel Principal / Dashboard</strong>
    </td>
    <td align="center">
      <img src="docs/assets/08_product_registration.png" alt="Product Registration" width="100%"/>
      <br/><strong>Registro de Productos</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/09_inventory_filters.png" alt="Inventory Filters" width="100%"/>
      <br/><strong>Inventario con Filtros Avanzados</strong>
    </td>
    <td align="center">
      <img src="docs/assets/06_client_registration.png" alt="Client Registration" width="100%"/>
      <br/><strong>Registro de Clientes</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/07_pet_registration.png" alt="Pet Registration" width="100%"/>
      <br/><strong>Registro de Mascotas</strong>
    </td>
    <td align="center">
      <img src="docs/assets/05_supplier_registration.png" alt="Supplier Registration" width="100%"/>
      <br/><strong>Registro de Proveedores</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/12_user_list_edit.png" alt="User Management" width="100%"/>
      <br/><strong>Gestión de Usuarios</strong>
    </td>
    <td align="center">
      <img src="docs/assets/11_user_permissions.png" alt="User Permissions" width="100%"/>
      <br/><strong>Permisos por Módulo</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/13_services_clinical.png" alt="Clinical Services" width="100%"/>
      <br/><strong>Servicios e Historial Clínico</strong>
    </td>
    <td align="center">
      <img src="docs/assets/03_otp_email.png" alt="OTP Email" width="100%"/>
      <br/><strong>Email OTP Recibido</strong>
    </td>
  </tr>
</table>

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| **Lenguaje** | Java 23 |
| **UI Framework** | JavaFX 17 (FXML + CSS) |
| **Build Tool** | Apache Maven 3.9 |
| **Base de Datos** | PostgreSQL 15+ |
| **Connection Pool** | HikariCP 5.0 |
| **Migraciones** | Flyway |
| **Seguridad** | BCrypt (hashing de contraseñas) |
| **Email** | JavaMail API (SMTP / Gmail) |
| **Patrón de Datos** | DAO con JDBC |
| **Arquitectura** | MVC (Model-View-Controller) |

## Arquitectura del Sistema

```mermaid
graph TD
    subgraph "🖥️ Capa de Presentación (View)"
        V1[FXML Views]
        V2[CSS Stylesheets]
    end

    subgraph "🧠 Capa de Control (Controller)"
        C1[LoginController]
        C2[DashboardController]
        C3[InventoryController]
        C4[SalesController]
        C5[ClientController]
        C6[UserController]
    end

    subgraph "⚙️ Capa de Servicio (Service)"
        S1[UsuarioService]
        S2[ProductoService]
        S3[VentaService]
        S4[ClienteMascotaService]
        S5[EmailService]
        S6[HistorialClinicaService]
    end

    subgraph "🗄️ Capa de Datos (Repository / DAO)"
        R1[UsuarioRepository]
        R2[ProductoRepository]
        R3[VentaRepository]
        R4[ClienteRepository]
        R5[MascotaRepository]
        R6[ProveedorRepository]
    end

    subgraph "🐘 Base de Datos"
        DB[(PostgreSQL<br/>Schema: veterinaria)]
    end

    V1 --> C1 & C2 & C3 & C4 & C5 & C6
    C1 --> S1 & S5
    C2 --> S1
    C3 --> S2
    C4 --> S3
    C5 --> S4
    C6 --> S1

    S1 --> R1
    S2 --> R2
    S3 --> R3
    S4 --> R4 & R5
    S6 --> R4

    R1 & R2 & R3 & R4 & R5 & R6 --> DB
```

## Modelo Entidad-Relación (ERD)

```mermaid
erDiagram
    USUARIO {
        int idusuario PK
        varchar cc
        varchar nombre
        varchar apellidos
        varchar usuario UK
        varchar email UK
        varchar contrasena
        varchar telefono
        varchar direccion
    }

    MODULO {
        int idmodulo PK
        varchar nombremodulo
        varchar descripcion
        varchar icono
        int orden
    }

    USUARIO_MODULO {
        int idusuario_modulo PK
        int usuario_idusuario FK
        int modulo_idmodulo FK
        timestamp fecha_asignacion
    }

    PASSWORD_RESET_TOKEN {
        int idtoken PK
        int usuario_idusuario FK
        varchar email
        varchar codigo_otp
        timestamp fecha_creacion
        timestamp fecha_expiracion
        boolean usado
    }

    CLIENTE {
        int idcliente PK
        varchar nombre
        varchar apellidos
        varchar tipopersona
        varchar cc
        date fechanacimiento
        varchar email
        varchar direccion
        varchar telefono
        varchar nombrecontactoemergencia
        varchar telefonocontactoemergencia
        varchar ciudad
        varchar tipodocumento
    }

    MASCOTA {
        int idmascota PK
        varchar nombre
        varchar raza
        varchar especie
        varchar sexo
        varchar numerochip
        int edad
        int cliente_idcliente FK
    }

    HISTORIALCLINICA {
        int idhistorialclinica PK
        text observaciones
        int mascota_idmascota FK
    }

    SERVICIO {
        int idservicio PK
        varchar nombreservicio
        numeric precio
        text descripcion
    }

    HISTORIALCLINICA_HAS_SERVICIO {
        int id PK
        int historialclinica_idhistorialclinica FK
        int servicio_idservicio FK
    }

    MARCA {
        int idmarca PK
        varchar nombremarca
        varchar descripcion
    }

    UNIDADMEDIDA {
        int idunidadmedida PK
        varchar nombre
    }

    TIPOPRODUCTO {
        int idtipoproducto PK
        varchar nombretipo
    }

    PRODUCTO {
        int idproducto PK
        varchar nombre
        varchar referencia
        varchar codigobarras
        numeric precio
        numeric costo
        text descripcion
        numeric stock
        int unidadesingresadas
        bytea imagenproducto
        varchar estado
        varchar lote
        date fechaVencimiento
        int semanaalerta
        boolean fraccionable
        boolean fraccionado
        numeric contenido
        varchar dosisUnidad
        int marca_idmarca FK
        int usuario_idusuario FK
        int unidadmedida_idunidadmedida FK
        int tipoproducto_idtipoproducto FK
    }

    PROVEEDOR {
        int idproveedor PK
        varchar tipopersona
        varchar tipodocumento
        varchar nit_rut
        varchar nombre
        varchar apellido
        varchar telefono
        varchar direccion
        varchar email
        varchar ciudad
    }

    PROVEEDOR_HAS_PRODUCTO {
        int id PK
        int producto_idproducto FK
        int proveedor_idproveedor FK
    }

    ALERTA {
        int idalerta PK
        boolean pendiente
        varchar motivo
        date fechaobjetivo
        date fechacreacion
        int producto_idproducto FK
    }

    VENTA {
        int idventa PK
        int cliente_idcliente FK
        timestamp fecha
        numeric totalventa
        int usuario_idusuario FK
    }

    LINEAVENTA {
        int idlineaventa PK
        int venta_idventa FK
        int producto_idproducto FK
        int servicio_idservicio FK
        int cantidad
        numeric subtotal
        numeric valor
    }

    USUARIO ||--o{ USUARIO_MODULO : "tiene permisos"
    MODULO ||--o{ USUARIO_MODULO : "asignado a"
    USUARIO ||--o{ PASSWORD_RESET_TOKEN : "solicita reset"
    USUARIO ||--o{ PRODUCTO : "registra"
    USUARIO ||--o{ VENTA : "realiza"

    CLIENTE ||--o{ MASCOTA : "tiene"
    CLIENTE ||--o{ VENTA : "compra"

    MASCOTA ||--o| HISTORIALCLINICA : "posee"
    HISTORIALCLINICA ||--o{ HISTORIALCLINICA_HAS_SERVICIO : "incluye"
    SERVICIO ||--o{ HISTORIALCLINICA_HAS_SERVICIO : "aplicado en"

    MARCA ||--o{ PRODUCTO : "categoriza"
    TIPOPRODUCTO ||--o{ PRODUCTO : "clasifica"
    UNIDADMEDIDA ||--o{ PRODUCTO : "mide"

    PRODUCTO ||--o{ PROVEEDOR_HAS_PRODUCTO : "suministrado por"
    PROVEEDOR ||--o{ PROVEEDOR_HAS_PRODUCTO : "suministra"
    PRODUCTO ||--o{ ALERTA : "genera"

    VENTA ||--o{ LINEAVENTA : "contiene"
    PRODUCTO ||--o{ LINEAVENTA : "vendido en"
    SERVICIO ||--o{ LINEAVENTA : "facturado en"
```

## Base de Datos

Para instrucciones detalladas de configuración de la base de datos, por favor consulta la **[Guía de Base de Datos (DATABASE_SETUP.md)](DATABASE_SETUP.md)**.

El script DDL completo se encuentra en [`sql/init_schema.sql`](sql/init_schema.sql). Los datos de prueba están en [`sql/seed_data.sql`](sql/seed_data.sql).

## Estructura del Proyecto

```
Veterinaria/
├── src/main/java/co/edu/upb/veterinaria/
│   ├── app/                    # Punto de entrada (Principal.java)
│   ├── config/                 # Configuración (DatabaseConfig)
│   ├── controllers/            # Controladores JavaFX (MVC)
│   ├── models/                 # Modelos / Entidades (POJOs)
│   ├── repositories/           # Capa DAO (acceso a BD con JDBC)
│   └── services/               # Lógica de negocio
├── src/main/resources/
│   └── co/edu/upb/veterinaria/
│       ├── views/              # Archivos FXML
│       ├── styles/             # Hojas de estilo CSS
│       └── images/             # Recursos gráficos
├── sql/
│   ├── init_schema.sql         # DDL completo de la base de datos
│   └── seed_data.sql           # Datos de prueba reales
├── docs/assets/                # Capturas de pantalla del sistema
├── .env.example                # Plantilla de variables de entorno
├── DATABASE_SETUP.md           # Guía de configuración de BD
├── CONTRIBUTING.md             # Guía de contribución
├── CODE_OF_CONDUCT.md          # Código de conducta
├── SECURITY.md                 # Política de seguridad
├── CHANGELOG.md                # Registro de cambios
├── LICENSE                     # Licencia MIT
└── pom.xml                     # Configuración Maven
```

## Inicio Rápido

### Prerrequisitos

- **Java JDK 23** o superior
- **Maven 3.9+**
- **PostgreSQL 15+** en ejecución

### Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/Veterinaria.git
cd Veterinaria

# 2. Configurar la base de datos (ver DATABASE_SETUP.md)
psql -U postgres -d postgres -f sql/init_schema.sql

# 3. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 4. Compilar y ejecutar
./mvnw clean javafx:run
```

## Funcionalidades Principales

- **Autenticación segura** con BCrypt y recuperación por email (OTP)
- **Gestión de usuarios** con permisos modulares granulares
- **Registro de clientes** con contacto de emergencia y mascotas asociadas
- **Inventario completo** (Medicamentos, Alimentos, Material Quirúrgico, Accesorios)
- **Punto de Venta (POS)** con líneas de venta para productos y servicios
- **Historial Clínico** por mascota con servicios asociados
- **Alertas automáticas** de vencimiento y stock bajo
- **Gestión de Proveedores** con relación N:N a productos

## 🤝 Contribuir

Las contribuciones son bienvenidas. Sigue estos pasos:

1. **Crea un fork** del repositorio.
2. **Crea una rama** para tu feature: `git checkout -b feature/mi-feature`.
3. **Haz commit** de tus cambios: `git commit -m "feat: descripción del cambio"`.
4. **Sube tu rama**: `git push origin feature/mi-feature`.
5. **Abre un Pull Request** describiendo tus cambios.

📖 Consulta la **[Guía de Contribución](CONTRIBUTING.md)** para convenciones de código, commits y flujo de trabajo.

🤝 Lee nuestro **[Código de Conducta](CODE_OF_CONDUCT.md)** antes de participar.

## 🔒 Seguridad

Si descubres una vulnerabilidad de seguridad, **NO abras un issue público**. Consulta nuestra **[Política de Seguridad](SECURITY.md)** para el proceso de reporte responsable.

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT** — libre para uso, modificación y distribución. Ver [`LICENSE`](LICENSE) para más información.

---

<p align="center">
  <strong>Hecho con ❤️ por el equipo de desarrollo</strong><br/>
  Universidad Pontificia Bolivariana<br/>
  <em>Proyecto de Aula · Ingeniería de Software · Ingeniería de Sistemas e Informática · 2025-2026</em>
</p>

---
---

# 🇺🇸 English

## About the Project

**Veterinaria SOS** is an enterprise resource planning (ERP) system designed for veterinary clinics. It was developed as an academic MVP for the **Software Engineering** course (4th semester) at **Universidad Pontificia Bolivariana (UPB)**, achieving a grade of **5.0/5.0**.

The system manages clients, pets, inventory (medications, food, accessories, surgical materials), suppliers, sales (POS), clinical history, users with modular permissions, and email-based password recovery with OTP codes.

## UI Showcase Gallery

<table>
  <tr>
    <td align="center" width="50%">
      <img src="docs/assets/01_login_view.png" alt="Login" width="100%"/>
      <br/><strong>Login Screen</strong>
    </td>
    <td align="center" width="50%">
      <img src="docs/assets/02_password_recovery.png" alt="Password Recovery" width="100%"/>
      <br/><strong>Password Recovery (OTP)</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/04_main_dashboard.png" alt="Dashboard" width="100%"/>
      <br/><strong>Main Dashboard</strong>
    </td>
    <td align="center">
      <img src="docs/assets/08_product_registration.png" alt="Product Registration" width="100%"/>
      <br/><strong>Product Registration</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/09_inventory_filters.png" alt="Inventory Filters" width="100%"/>
      <br/><strong>Inventory with Advanced Filters</strong>
    </td>
    <td align="center">
      <img src="docs/assets/06_client_registration.png" alt="Client Registration" width="100%"/>
      <br/><strong>Client Registration</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/07_pet_registration.png" alt="Pet Registration" width="100%"/>
      <br/><strong>Pet Registration</strong>
    </td>
    <td align="center">
      <img src="docs/assets/05_supplier_registration.png" alt="Supplier Registration" width="100%"/>
      <br/><strong>Supplier Registration</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/12_user_list_edit.png" alt="User Management" width="100%"/>
      <br/><strong>User Management</strong>
    </td>
    <td align="center">
      <img src="docs/assets/11_user_permissions.png" alt="User Permissions" width="100%"/>
      <br/><strong>Module Permissions</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/assets/13_services_clinical.png" alt="Clinical Services" width="100%"/>
      <br/><strong>Services & Clinical History</strong>
    </td>
    <td align="center">
      <img src="docs/assets/03_otp_email.png" alt="OTP Email" width="100%"/>
      <br/><strong>OTP Email Received</strong>
    </td>
  </tr>
</table>

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 23 |
| **UI Framework** | JavaFX 17 (FXML + CSS) |
| **Build Tool** | Apache Maven 3.9 |
| **Database** | PostgreSQL 15+ |
| **Connection Pool** | HikariCP 5.0 |
| **Migrations** | Flyway |
| **Security** | BCrypt (password hashing) |
| **Email** | JavaMail API (SMTP / Gmail) |
| **Data Pattern** | DAO with JDBC |
| **Architecture** | MVC (Model-View-Controller) |

## System Architecture

```mermaid
graph TD
    subgraph "Presentation Layer - View"
        V1[FXML Views]
        V2[CSS Stylesheets]
    end

    subgraph "Control Layer - Controller"
        C1[LoginController]
        C2[DashboardController]
        C3[InventoryController]
        C4[SalesController]
        C5[ClientController]
        C6[UserController]
    end

    subgraph "Service Layer"
        S1[UsuarioService]
        S2[ProductoService]
        S3[VentaService]
        S4[ClienteMascotaService]
        S5[EmailService]
        S6[HistorialClinicaService]
    end

    subgraph "Data Layer - Repository / DAO"
        R1[UsuarioRepository]
        R2[ProductoRepository]
        R3[VentaRepository]
        R4[ClienteRepository]
        R5[MascotaRepository]
        R6[ProveedorRepository]
    end

    subgraph "Database"
        DB[(PostgreSQL<br/>Schema: veterinaria)]
    end

    V1 --> C1 & C2 & C3 & C4 & C5 & C6
    C1 --> S1 & S5
    C2 --> S1
    C3 --> S2
    C4 --> S3
    C5 --> S4
    C6 --> S1

    S1 --> R1
    S2 --> R2
    S3 --> R3
    S4 --> R4 & R5
    S6 --> R4

    R1 & R2 & R3 & R4 & R5 & R6 --> DB
```

## Entity-Relationship Diagram (ERD)

```mermaid
erDiagram
    USUARIO {
        int idusuario PK
        varchar cc
        varchar nombre
        varchar apellidos
        varchar usuario UK
        varchar email UK
        varchar contrasena
        varchar telefono
        varchar direccion
    }

    MODULO {
        int idmodulo PK
        varchar nombremodulo
        varchar descripcion
        varchar icono
        int orden
    }

    USUARIO_MODULO {
        int idusuario_modulo PK
        int usuario_idusuario FK
        int modulo_idmodulo FK
        timestamp fecha_asignacion
    }

    PASSWORD_RESET_TOKEN {
        int idtoken PK
        int usuario_idusuario FK
        varchar email
        varchar codigo_otp
        timestamp fecha_creacion
        timestamp fecha_expiracion
        boolean usado
    }

    CLIENTE {
        int idcliente PK
        varchar nombre
        varchar apellidos
        varchar tipopersona
        varchar cc
        date fechanacimiento
        varchar email
        varchar direccion
        varchar telefono
        varchar nombrecontactoemergencia
        varchar telefonocontactoemergencia
        varchar ciudad
        varchar tipodocumento
    }

    MASCOTA {
        int idmascota PK
        varchar nombre
        varchar raza
        varchar especie
        varchar sexo
        varchar numerochip
        int edad
        int cliente_idcliente FK
    }

    HISTORIALCLINICA {
        int idhistorialclinica PK
        text observaciones
        int mascota_idmascota FK
    }

    SERVICIO {
        int idservicio PK
        varchar nombreservicio
        numeric precio
        text descripcion
    }

    HISTORIALCLINICA_HAS_SERVICIO {
        int id PK
        int historialclinica_idhistorialclinica FK
        int servicio_idservicio FK
    }

    MARCA {
        int idmarca PK
        varchar nombremarca
        varchar descripcion
    }

    UNIDADMEDIDA {
        int idunidadmedida PK
        varchar nombre
    }

    TIPOPRODUCTO {
        int idtipoproducto PK
        varchar nombretipo
    }

    PRODUCTO {
        int idproducto PK
        varchar nombre
        varchar referencia
        varchar codigobarras
        numeric precio
        numeric costo
        text descripcion
        numeric stock
        int unidadesingresadas
        bytea imagenproducto
        varchar estado
        varchar lote
        date fechaVencimiento
        int semanaalerta
        boolean fraccionable
        boolean fraccionado
        numeric contenido
        varchar dosisUnidad
        int marca_idmarca FK
        int usuario_idusuario FK
        int unidadmedida_idunidadmedida FK
        int tipoproducto_idtipoproducto FK
    }

    PROVEEDOR {
        int idproveedor PK
        varchar tipopersona
        varchar tipodocumento
        varchar nit_rut
        varchar nombre
        varchar apellido
        varchar telefono
        varchar direccion
        varchar email
        varchar ciudad
    }

    PROVEEDOR_HAS_PRODUCTO {
        int id PK
        int producto_idproducto FK
        int proveedor_idproveedor FK
    }

    ALERTA {
        int idalerta PK
        boolean pendiente
        varchar motivo
        date fechaobjetivo
        date fechacreacion
        int producto_idproducto FK
    }

    VENTA {
        int idventa PK
        int cliente_idcliente FK
        timestamp fecha
        numeric totalventa
        int usuario_idusuario FK
    }

    LINEAVENTA {
        int idlineaventa PK
        int venta_idventa FK
        int producto_idproducto FK
        int servicio_idservicio FK
        int cantidad
        numeric subtotal
        numeric valor
    }

    USUARIO ||--o{ USUARIO_MODULO : "has permissions"
    MODULO ||--o{ USUARIO_MODULO : "assigned to"
    USUARIO ||--o{ PASSWORD_RESET_TOKEN : "requests reset"
    USUARIO ||--o{ PRODUCTO : "registers"
    USUARIO ||--o{ VENTA : "makes"

    CLIENTE ||--o{ MASCOTA : "owns"
    CLIENTE ||--o{ VENTA : "purchases"

    MASCOTA ||--o| HISTORIALCLINICA : "has"
    HISTORIALCLINICA ||--o{ HISTORIALCLINICA_HAS_SERVICIO : "includes"
    SERVICIO ||--o{ HISTORIALCLINICA_HAS_SERVICIO : "applied in"

    MARCA ||--o{ PRODUCTO : "categorizes"
    TIPOPRODUCTO ||--o{ PRODUCTO : "classifies"
    UNIDADMEDIDA ||--o{ PRODUCTO : "measures"

    PRODUCTO ||--o{ PROVEEDOR_HAS_PRODUCTO : "supplied by"
    PROVEEDOR ||--o{ PROVEEDOR_HAS_PRODUCTO : "supplies"
    PRODUCTO ||--o{ ALERTA : "triggers"

    VENTA ||--o{ LINEAVENTA : "contains"
    PRODUCTO ||--o{ LINEAVENTA : "sold in"
    SERVICIO ||--o{ LINEAVENTA : "billed in"
```

## Database

For detailed database setup instructions, please refer to the **[Database Setup Guide (DATABASE_SETUP.md)](DATABASE_SETUP.md)**.

The complete DDL script is available at [`sql/init_schema.sql`](sql/init_schema.sql). Test data is in [`sql/seed_data.sql`](sql/seed_data.sql).

## Project Structure

```
Veterinaria/
├── src/main/java/co/edu/upb/veterinaria/
│   ├── app/                    # Entry point (Principal.java)
│   ├── config/                 # Configuration (DatabaseConfig)
│   ├── controllers/            # JavaFX Controllers (MVC)
│   ├── models/                 # Models / Entities (POJOs)
│   ├── repositories/           # DAO Layer (DB access with JDBC)
│   └── services/               # Business logic
├── src/main/resources/
│   └── co/edu/upb/veterinaria/
│       ├── views/              # FXML files
│       ├── styles/             # CSS stylesheets
│       └── images/             # Graphic resources
├── sql/
│   ├── init_schema.sql         # Full database DDL
│   └── seed_data.sql           # Real test data
├── docs/assets/                # System screenshots
├── .env.example                # Environment variables template
├── DATABASE_SETUP.md           # Database setup guide
├── CONTRIBUTING.md             # Contribution guide
├── CODE_OF_CONDUCT.md          # Code of conduct
├── SECURITY.md                 # Security policy
├── CHANGELOG.md                # Changelog
├── LICENSE                     # MIT License
└── pom.xml                     # Maven configuration
```

## Quick Start

### Prerequisites

- **Java JDK 23** or higher
- **Maven 3.9+**
- **PostgreSQL 15+** running

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-user/Veterinaria.git
cd Veterinaria

# 2. Set up the database (see DATABASE_SETUP.md)
psql -U postgres -d postgres -f sql/init_schema.sql

# 3. Configure environment variables
cp .env.example .env
# Edit .env with your credentials

# 4. Build and run
./mvnw clean javafx:run
```

## Key Features

- **Secure authentication** with BCrypt and email recovery (OTP)
- **User management** with granular modular permissions
- **Client registration** with emergency contacts and associated pets
- **Complete inventory** (Medications, Food, Surgical Materials, Accessories)
- **Point of Sale (POS)** with sale lines for products and services
- **Clinical History** per pet with associated services
- **Automatic alerts** for expiration and low stock
- **Supplier management** with N:N product relationships

## 🤝 Contributing

Contributions are welcome. Follow these steps:

1. **Fork** the repository.
2. **Create a branch** for your feature: `git checkout -b feature/my-feature`.
3. **Commit** your changes: `git commit -m "feat: description of change"`.
4. **Push** your branch: `git push origin feature/my-feature`.
5. **Open a Pull Request** describing your changes.

📖 Check the **[Contribution Guide](CONTRIBUTING.md)** for code conventions, commits, and workflow.

🤝 Read our **[Code of Conduct](CODE_OF_CONDUCT.md)** before participating.

## 🔒 Security

If you discover a security vulnerability, **DO NOT open a public issue**. See our **[Security Policy](SECURITY.md)** for the responsible disclosure process.

## 📄 License

This project is licensed under the **MIT License** — free for use, modification, and distribution. See [`LICENSE`](LICENSE) for more information.

---

<p align="center">
  <strong>Made with ❤️ by the development team</strong><br/>
  Universidad Pontificia Bolivariana<br/>
  <em>Classroom Project · Software Engineering · Systems and Informatics Engineering · 2025-2026</em>
</p>
