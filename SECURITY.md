# 🔒 Política de Seguridad / Security Policy

---

# 🇪🇸 Español

## Versiones Soportadas

| Versión | Soportada |
|---|---|
| 1.0-SNAPSHOT (actual) | ✅ |

## Reportar una Vulnerabilidad

Si descubres una vulnerabilidad de seguridad en este proyecto, por favor **NO** la publiques en un Issue público.

En su lugar:

1. Abre un **Issue privado** o envía un correo al equipo del proyecto.
2. Incluye una descripción detallada de la vulnerabilidad.
3. Si es posible, incluye pasos para reproducir el problema.
4. Nos comprometemos a responder dentro de **48 horas**.

## Buenas Prácticas Implementadas

- **Credenciales:** Todas las contraseñas y tokens se leen desde variables de entorno (`System.getenv()`). Ver `.env.example`.
- **Hashing de contraseñas:** Se usa **BCrypt** para almacenar contraseñas de usuarios.
- **Connection Pool:** HikariCP gestiona las conexiones a la BD de forma segura.
- **OTP:** Los tokens de recuperación de contraseña expiran en 15 minutos.
- **SMTP seguro:** Conexiones SMTP con TLS 1.2.

## Lo que NUNCA debe ir en el repositorio

- Contraseñas en texto plano.
- Tokens de API o claves secretas.
- Archivos `.env` con datos reales.
- Dumps de base de datos con datos personales.

---
---

# 🇺🇸 English

## Supported Versions

| Version | Supported |
|---|---|
| 1.0-SNAPSHOT (current) | ✅ |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please **DO NOT** publish it in a public Issue.

Instead:

1. Open a **private Issue** or email the project team.
2. Include a detailed description of the vulnerability.
3. If possible, include steps to reproduce the issue.
4. We commit to responding within **48 hours**.

## Security Best Practices Implemented

- **Credentials:** All passwords and tokens are read from environment variables (`System.getenv()`). See `.env.example`.
- **Password Hashing:** **BCrypt** is used to store user passwords.
- **Connection Pool:** HikariCP manages DB connections securely.
- **OTP:** Password reset tokens expire in 15 minutes.
- **Secure SMTP:** SMTP connections with TLS 1.2.

## What should NEVER be in the repository

- Plaintext passwords.
- API tokens or secret keys.
- `.env` files with real data.
- Database dumps with personal data.
