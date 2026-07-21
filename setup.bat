@echo off
setlocal
echo ==========================================
echo   Floating Companion - Quick Setup
echo ==========================================
echo.

set JDK_ZIP=jdk21.zip
set JDK_DIR=jdk21
set JDK_VERSION_DIR=jdk-21.0.4+7
set JDK_URL=https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.4%%2B7/OpenJDK21U-jdk_x64_windows_hotspot_21.0.4_7.zip

if exist "%JDK_DIR%\%JDK_VERSION_DIR%" (
    echo [1/3] JDK 21 is already installed in %JDK_DIR%.
) else (
    echo [1/3] Downloading JDK 21...
    if not exist "%JDK_ZIP%" (
        powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%JDK_URL%' -OutFile '%JDK_ZIP%'"
    )
    
    echo [2/3] Extracting JDK 21...
    if not exist "%JDK_DIR%" mkdir "%JDK_DIR%"
    powershell -Command "Expand-Archive -Path '%JDK_ZIP%' -DestinationPath '%JDK_DIR%' -Force"
)

echo.
echo [3/3] Downloading Gradle dependencies and building...
set JAVA_HOME=%CD%\%JDK_DIR%\%JDK_VERSION_DIR%
call gradlew.bat classes

echo.
echo ==========================================
echo Setup Complete! 
echo You can now run the application using: run_app.bat
echo ==========================================
pause
