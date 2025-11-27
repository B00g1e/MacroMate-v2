@echo off
title MacroMate Server - Build (using mvnd)
color 0B

echo ========================================
echo MacroMate Server - Build Script
echo Using Maven Daemon (mvnd) for faster builds
echo ========================================
echo.

cd /d "%~dp0"

set MVND_HOME=C:\Users\TUF\Desktop\maven-mvnd-1.0.3-windows-amd64
set PATH=%MVND_HOME%\bin;%PATH%

echo Checking mvnd installation...
if not exist "%MVND_HOME%\bin\mvnd.cmd" (
    echo ERROR: mvnd not found at %MVND_HOME%
    echo Please check the path.
    pause
    exit /b 1
)

echo.
echo Maven Daemon version:
call "%MVND_HOME%\bin\mvnd.cmd" --version
echo.

echo Building MacroMate Server...
echo This may take a few minutes on first run...
echo.

call "%MVND_HOME%\bin\mvnd.cmd" clean package

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
