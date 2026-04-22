@echo off
echo ========================================
echo Reconstruyendo proyecto Veterinaria
echo ========================================
echo.

call mvnw.cmd clean compile

echo.
echo ========================================
echo Proyecto reconstruido exitosamente
echo ========================================
pause

