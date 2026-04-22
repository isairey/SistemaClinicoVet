@echo off
REM =====================================================================
REM Script para crear ejecutable .exe de Veterinaria SOS con jpackage
REM =====================================================================

echo.
echo ========================================
echo   Generando ejecutable Windows (.exe)
echo ========================================
echo.

REM Verificar que jpackage esté disponible (incluido en JDK 16+)
where jpackage >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: jpackage no está disponible.
    echo Asegúrate de tener JDK 16 o superior instalado.
    pause
    exit /b 1
)

REM Crear el ejecutable con jpackage
jpackage ^
    --type exe ^
    --name "VeterinariaSOS" ^
    --app-version "1.0" ^
    --vendor "Universidad Pontificia Bolivariana" ^
    --description "Sistema integral de gestión para clínicas veterinarias" ^
    --icon "src\main\resources\co\edu\upb\veterinaria\images\logoSecundario.ico" ^
    --input "release" ^
    --main-jar "VeterinariaSOS.jar" ^
    --main-class "co.edu.upb.veterinaria.app.Principal" ^
    --java-options "--add-reads com.veterinaria.veterinaria=ALL-UNNAMED" ^
    --java-options "--add-opens com.veterinaria.veterinaria/co.edu.upb.veterinaria.services.ServicioEmail=ALL-UNNAMED" ^
    --dest "release" ^
    --win-dir-chooser ^
    --win-menu ^
    --win-shortcut

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   Ejecutable creado exitosamente!
    echo   Ubicación: release\VeterinariaSOS.exe
    echo   El instalador incluye el logo de la veterinaria
    echo ========================================
) else (
    echo.
    echo ERROR: No se pudo crear el ejecutable.
    echo Verifica que el archivo logoSecundario.ico exista
)

pause
