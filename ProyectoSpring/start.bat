@echo off
REM Script para configurar e iniciar la aplicación SaaS Platform en Windows

setlocal enabledelayedexpansion

echo.
echo ======================================
echo   SaaS Platform - Setup ^& Run
echo ======================================
echo.

REM Verificar Java
echo Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo Java no está instalado
    exit /b 1
)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /r "version"') do (
    set JAVA_VERSION=%%g
)
echo Java %JAVA_VERSION% encontrado
echo.

REM Verificar Maven wrapper
echo Verificando Maven...
if not exist "mvnw.cmd" (
    echo Maven wrapper no encontrado. Por favor ejecuta desde la raíz del proyecto.
    exit /b 1
)
echo Maven wrapper encontrado
echo.

REM Verificar MySQL
echo Verificando conexión a MySQL...
echo Por favor asegúrate de que MySQL esté corriendo en localhost:3306
echo.

REM Compilar
echo Compilando proyecto...
call mvnw.cmd clean install -q
if errorlevel 1 (
    echo Error en la compilación
    exit /b 1
)
echo Compilación exitosa
echo.

REM Iniciar la aplicación
echo Iniciando aplicación...
echo La aplicación estará disponible en: http://localhost:8080
echo.
call mvnw.cmd spring-boot:run
