@echo off
REM Script to run Testcontainers tests on Windows

echo Testcontainers Test Runner (Windows)
echo ====================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Docker is not running or not accessible
    echo.
    echo Please ensure:
    echo 1. Docker Desktop is running
    echo 2. Expose daemon on tcp://localhost:2375 without TLS is enabled in Docker Desktop settings
    echo    OR run this from WSL2: wsl ./run-tests.sh
    echo.
    pause
    exit /b 1
)

echo Docker is accessible
docker version
echo.

REM Set Docker host for Windows named pipe
if "%DOCKER_HOST%"=="" (
    set DOCKER_HOST=npipe:////./pipe/docker_engine
    echo Using Docker named pipe: npipe:////./pipe/docker_engine
)

echo.
echo Running Maven tests...
echo.

REM Run tests
mvn clean test -Dspring.profiles.active=test

echo.
echo Tests completed!
pause
