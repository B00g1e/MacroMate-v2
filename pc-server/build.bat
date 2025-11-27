@echo off
title MacroMate Server - Build
color 0B

echo ========================================
echo MacroMate Server - Build Script
echo ========================================
echo.

cd /d "%~dp0"

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed!
    echo.
    echo Please download and install Maven from:
    echo https://maven.apache.org/download.cgi
    echo.
    echo Or use the provided Maven wrapper if available.
    pause
    exit /b 1
)

echo Maven version:
mvn --version
echo.

echo Building MacroMate Server...
echo This may take a few minutes on first run...
echo.

mvn clean package

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build Successful!
echo ========================================
echo.
echo JAR file created in: target\macromate-server-1.0.0.jar
echo.
echo You can now run the server using run.bat
echo.
pause
