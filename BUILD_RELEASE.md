# 🏗️ Guía de Construcción de Release / Build Release Guide

---

## 🇪🇸 Español

### Requisitos Previos

- **JDK 23** instalado (incluye `jpackage`)
- **Maven 3.9+** instalado
- **Windows 10+** (para generar `.exe`)

### Paso 1: Construir el JAR Ejecutable

```bash
# Limpiar y compilar el proyecto
./mvnw.cmd clean package -DskipTests

# El JAR se generará en: target/VeterinariaSOS-1.0-SNAPSHOT.jar
```

### Paso 2: Preparar la Carpeta Release

```bash
# Crear carpeta release (si no existe)
mkdir release

# Copiar el JAR a release/
copy target\VeterinariaSOS-1.0-SNAPSHOT.jar release\VeterinariaSOS.jar

# Copiar el script de lanzamiento
copy release\VeterinariaSOS.bat release\
```

### Paso 3: (Opcional) Generar Ejecutable Windows `.exe`

Para generar un ejecutable nativo de Windows con el logo de la veterinaria:

#### Opción A: Usando `jpackage` (Recomendado)

```bash
# Ejecutar el script de construcción
build-exe.bat
```

**Nota:** `jpackage` requiere un archivo `.ico` para el icono. Si no tienes un `.ico`, puedes:
1. Convertir `logoPrincipalPrincipal.png` a `.ico` usando una herramienta online como [convertio.co](https://convertio.co/es/png-ico/)
2. Guardar el `.ico` en `src/main/resources/co/edu/upb/veterinaria/images/logo.ico`
3. Actualizar `build-exe.bat` para usar `--icon "src\main\resources\co\edu\upb\veterinaria\images\logo.ico"`

#### Opción B: Usando Launch4j (Alternativa)

1. Descarga [Launch4j](https://launch4j.sourceforge.net/)
2. Configura:
   - **Output file:** `release/VeterinariaSOS.exe`
   - **Jar:** `release/VeterinariaSOS.jar`
   - **Icon:** `src/main/resources/co/edu/upb/veterinaria/images/logoPrincipalPrincipal.png` (convertido a `.ico`)
   - **JVM Options:**
     ```
     --add-reads com.veterinaria.veterinaria=ALL-UNNAMED
     --add-opens com.veterinaria.veterinaria/co.edu.upb.veterinaria.services.ServicioEmail=ALL-UNNAMED
     ```
3. Genera el `.exe`

### Paso 4: Probar el Release

```bash
# Opción 1: Ejecutar el script .bat
cd release
VeterinariaSOS.bat

# Opción 2: Ejecutar el .exe (si lo generaste)
VeterinariaSOS.exe
```

### Paso 5: Crear Release en GitHub

1. Crea un tag de versión:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0 - Primera versión estable"
   git push origin v1.0.0
   ```

2. Ve a GitHub → **Releases** → **Draft a new release**

3. Sube los siguientes archivos:
   - `VeterinariaSOS.jar`
   - `VeterinariaSOS.bat`
   - `VeterinariaSOS.exe` (si lo generaste)
   - `README.md` (de la carpeta release)

4. Escribe las notas de la release y publica.

---

## 🇺🇸 English

### Prerequisites

- **JDK 23** installed (includes `jpackage`)
- **Maven 3.9+** installed
- **Windows 10+** (to generate `.exe`)

### Step 1: Build the Executable JAR

```bash
# Clean and compile the project
./mvnw.cmd clean package -DskipTests

# The JAR will be generated at: target/VeterinariaSOS-1.0-SNAPSHOT.jar
```

### Step 2: Prepare the Release Folder

```bash
# Create release folder (if it doesn't exist)
mkdir release

# Copy the JAR to release/
copy target\VeterinariaSOS-1.0-SNAPSHOT.jar release\VeterinariaSOS.jar

# Copy the launcher script
copy release\VeterinariaSOS.bat release\
```

### Step 3: (Optional) Generate Windows `.exe` Executable

To generate a native Windows executable with the veterinary logo:

#### Option A: Using `jpackage` (Recommended)

```bash
# Run the build script
build-exe.bat
```

**Note:** `jpackage` requires an `.ico` file for the icon. If you don't have an `.ico`, you can:
1. Convert `logoPrincipalPrincipal.png` to `.ico` using an online tool like [convertio.co](https://convertio.co/png-ico/)
2. Save the `.ico` at `src/main/resources/co/edu/upb/veterinaria/images/logo.ico`
3. Update `build-exe.bat` to use `--icon "src\main\resources\co\edu\upb\veterinaria\images\logo.ico"`

#### Option B: Using Launch4j (Alternative)

1. Download [Launch4j](https://launch4j.sourceforge.net/)
2. Configure:
   - **Output file:** `release/VeterinariaSOS.exe`
   - **Jar:** `release/VeterinariaSOS.jar`
   - **Icon:** `src/main/resources/co/edu/upb/veterinaria/images/logoPrincipalPrincipal.png` (converted to `.ico`)
   - **JVM Options:**
     ```
     --add-reads com.veterinaria.veterinaria=ALL-UNNAMED
     --add-opens com.veterinaria.veterinaria/co.edu.upb.veterinaria.services.ServicioEmail=ALL-UNNAMED
     ```
3. Generate the `.exe`

### Step 4: Test the Release

```bash
# Option 1: Run the .bat script
cd release
VeterinariaSOS.bat

# Option 2: Run the .exe (if you generated it)
VeterinariaSOS.exe
```

### Step 5: Create GitHub Release

1. Create a version tag:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0 - First stable version"
   git push origin v1.0.0
   ```

2. Go to GitHub → **Releases** → **Draft a new release**

3. Upload the following files:
   - `VeterinariaSOS.jar`
   - `VeterinariaSOS.bat`
   - `VeterinariaSOS.exe` (if you generated it)
   - `README.md` (from the release folder)

4. Write the release notes and publish.

---

## 📦 Contenido del Release / Release Contents

El release debe incluir:

- **`VeterinariaSOS.jar`** (65 MB) — Aplicación con todas las dependencias
- **`VeterinariaSOS.bat`** — Script de lanzamiento para Windows
- **`VeterinariaSOS.exe`** (opcional) — Ejecutable nativo de Windows con logo
- **`README.md`** — Instrucciones de instalación y uso

---

## 🐛 Solución de Problemas / Troubleshooting

### Error: "maven-shade-plugin not found"
```bash
# Actualizar Maven
./mvnw.cmd clean install
```

### Error: "jpackage command not found"
- Asegúrate de tener JDK 16+ instalado (no JRE).
- Verifica que `JAVA_HOME` apunte al JDK.

### El `.exe` no muestra el icono
- Convierte el logo PNG a ICO usando una herramienta online.
- Actualiza `build-exe.bat` con la ruta correcta al `.ico`.

---

<p align="center">
  <strong>Hecho con ❤️ por el equipo de desarrollo</strong><br/>
  Universidad Pontificia Bolivariana
</p>
