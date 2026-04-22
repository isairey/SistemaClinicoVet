@echo off
REM =====================================================================
REM Script simplificado para crear ejecutable .exe usando jpackage
REM sin instalador (solo app-image)
REM =====================================================================

echo.
echo ========================================
echo   Generando ejecutable Windows (.exe)
echo   Modo: Aplicación portable (sin instalador)
echo ========================================
echo.

REM Verificar que jpackage esté disponible
where jpackage >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: jpackage no está disponible.
    echo Asegúrate de tener JDK 16 o superior instalado.
    pause
    exit /b 1
)

REM Crear imagen de aplicación (portable, sin instalador)
jpackage ^
    --type app-image ^
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
    --dest "release"

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   Aplicación creada exitosamente!
    echo   Ubicación: release\VeterinariaSOS\VeterinariaSOS.exe
    echo   El ejecutable incluye el logo de la veterinaria
    echo.
    echo   Puedes distribuir toda la carpeta VeterinariaSOS
    echo   o crear un ZIP para compartir.
    echo ========================================
    
    REM Copiar el .exe principal a la raíz de release para fácil acceso
    if exist "release\VeterinariaSOS\VeterinariaSOS.exe" (
        echo.
        echo Copiando ejecutable a release\VeterinariaSOS.exe...
        copy "release\VeterinariaSOS\VeterinariaSOS.exe" "release\VeterinariaSOS.exe" >nul
        echo Listo!
    )
) else (
    echo.
    echo ERROR: No se pudo crear el ejecutable.
    echo Verifica que el archivo logoSecundario.ico exista
)

pause
