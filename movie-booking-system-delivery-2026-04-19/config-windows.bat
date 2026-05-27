@echo off
setlocal EnableExtensions

for %%I in ("%~dp0.") do set "ROOT_DIR=%%~fI"
if not defined ROOT_DIR set "ROOT_DIR=%~dp0"
if "%ROOT_DIR:~-1%"=="\" set "ROOT_DIR=%ROOT_DIR:~0,-1%"
for /f "usebackq delims=" %%I in (`powershell -NoProfile -Command "(Get-Item -LiteralPath '%ROOT_DIR%').FullName" 2^>nul`) do set "ROOT_DIR=%%I"

rem ==== database config ====
set "DB_HOST=127.0.0.1"
set "DB_PORT=3306"
set "DB_NAME=movie_booking_system"
set "DB_USER=root"
set "DB_PASSWORD=root"

rem ==== service ports ====
set "SERVER_PORT=1000"
set "FRONT_PORT=5173"

rem ==== executable commands ====
set "MAVEN_BIN=mvn"
set "NODE_BIN=node"
set "NPM_BIN=npm"

rem ==== frontend api url ====
set "VITE_APP_API_URL=http://127.0.0.1:%SERVER_PORT%"

endlocal & (
  set "ROOT_DIR=%ROOT_DIR%"
  set "DB_HOST=%DB_HOST%"
  set "DB_PORT=%DB_PORT%"
  set "DB_NAME=%DB_NAME%"
  set "DB_USER=%DB_USER%"
  set "DB_PASSWORD=%DB_PASSWORD%"
  set "SERVER_PORT=%SERVER_PORT%"
  set "FRONT_PORT=%FRONT_PORT%"
  set "MAVEN_BIN=%MAVEN_BIN%"
  set "NODE_BIN=%NODE_BIN%"
  set "NPM_BIN=%NPM_BIN%"
  set "VITE_APP_API_URL=%VITE_APP_API_URL%"
)
