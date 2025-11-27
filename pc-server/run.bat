@echo off
title MacroMate Server
color 0A

echo ========================================
echo MacroMate Server
echo ========================================
echo.

cd /d "%~dp0"

REM Check if JAR file exists
if not exist "target\macromate-server-1.0.0.jar" (
    echo ERROR: JAR file not found!
    echo.
    echo Please run build.bat first to compile the server.
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed!
    echo.
    echo Please download and install Java JDK 11 or higher from:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo Java version:
java -version
echo.

echo Starting MacroMate Server...
echo.

java -jar target\macromate-server-1.0.0.jar

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Server failed to start!
    pause
    exit /b 1
)
