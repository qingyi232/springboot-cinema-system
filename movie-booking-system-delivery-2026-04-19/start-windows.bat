@echo off
setlocal EnableExtensions

call "%~dp0config-windows.bat"

set "LOG_DIR=%ROOT_DIR%\logs"
set "BACKEND_LOG=%LOG_DIR%\backend.log"
set "FRONTEND_LOG=%LOG_DIR%\frontend.log"
set "FRONTEND_BUILD_LOG=%LOG_DIR%\frontend-build.log"
set "FRONT_DIST_DIR=%ROOT_DIR%\web\dist"
set "FRONT_DIST_INDEX=%FRONT_DIST_DIR%\index.html"
set "FRONT_RUNTIME_CONFIG=%FRONT_DIST_DIR%\runtime-config.js"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
set "APP_CHECK_DB="
set "APP_INIT_DB="

where java >nul 2>nul
if errorlevel 1 goto java_error

where %NODE_BIN% >nul 2>nul
if errorlevel 1 goto node_error

echo [INFO] Clearing stale processes on target ports ...
call :kill_port %SERVER_PORT%
call :kill_port %FRONT_PORT%

call :prepare_frontend
if errorlevel 1 exit /b 1

set "DB_CHECK_EXIT_CODE=0"
if exist "%ROOT_DIR%\target\template.jar" (
  echo [INFO] Checking database connectivity ...
  pushd "%ROOT_DIR%"
  set "DB_URL=jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
  set "DB_USERNAME=%DB_USER%"
  set "APP_CHECK_DB=true"
  java -jar target\template.jar --app.check-db=true
  set "DB_CHECK_EXIT_CODE=%ERRORLEVEL%"
  popd
)
set "APP_CHECK_DB="
if not "%DB_CHECK_EXIT_CODE%"=="0" goto db_precheck_error

echo [INFO] Starting backend service ...
if exist "%BACKEND_LOG%" del /f /q "%BACKEND_LOG%" >nul 2>nul
set "DB_URL=jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
set "DB_USERNAME=%DB_USER%"
set "DB_PASSWORD=%DB_PASSWORD%"
set "SERVER_PORT=%SERVER_PORT%"
if exist "%ROOT_DIR%\target\template.jar" (
  start "movie-booking-backend" /MIN /D "%ROOT_DIR%" cmd /c java -jar target\template.jar ^> "%BACKEND_LOG%" 2^>^&1
) else (
  where %MAVEN_BIN% >nul 2>nul
  if errorlevel 1 goto maven_error
  start "movie-booking-backend" /MIN /D "%ROOT_DIR%" cmd /c %MAVEN_BIN% spring-boot:run ^> "%BACKEND_LOG%" 2^>^&1
)

call :wait_for_port %SERVER_PORT% 25
if errorlevel 1 goto backend_start_error

echo [INFO] Starting frontend service ...
if exist "%FRONTEND_LOG%" del /f /q "%FRONTEND_LOG%" >nul 2>nul
call :write_frontend_runtime_config
if errorlevel 1 goto frontend_runtime_config_error
start "movie-booking-front" /MIN /D "%ROOT_DIR%\web" cmd /c %NODE_BIN% server-prod.js --host 0.0.0.0 --port %FRONT_PORT% ^> "%FRONTEND_LOG%" 2^>^&1

call :wait_for_port %FRONT_PORT% 20
if errorlevel 1 goto frontend_start_error

echo.
echo [SUCCESS] Backend and frontend are running.
echo [SUCCESS] Backend URL: http://127.0.0.1:%SERVER_PORT%
echo [SUCCESS] Frontend URL: http://127.0.0.1:%FRONT_PORT%
echo [SUCCESS] Backend log: %BACKEND_LOG%
echo [SUCCESS] Frontend log: %FRONTEND_LOG%
if exist "%FRONTEND_BUILD_LOG%" echo [SUCCESS] Frontend build log: %FRONTEND_BUILD_LOG%
echo.
echo [TIP] Admin: admin / 123456
echo [TIP] User: user1 / 123456
echo [TIP] Cinema: c1 / 123456 ^| c2 / 123456 ^| c3 / 123456
pause
exit /b 0

:prepare_frontend
if exist "%FRONT_DIST_INDEX%" (
  echo [INFO] Using packaged frontend dist assets.
  exit /b 0
)

echo [WARN] Prebuilt frontend dist was not found.
echo [WARN] Trying to build frontend assets locally ...
where %NPM_BIN% >nul 2>nul
if errorlevel 1 goto prepare_frontend_no_npm

echo [INFO] Checking frontend dependencies ...
pushd "%ROOT_DIR%\web"
call %NPM_BIN% install
set "NPM_INSTALL_EXIT_CODE=%ERRORLEVEL%"
popd
if not "%NPM_INSTALL_EXIT_CODE%"=="0" goto prepare_frontend_npm_install_error

echo [INFO] Building frontend assets ...
if exist "%FRONTEND_BUILD_LOG%" del /f /q "%FRONTEND_BUILD_LOG%" >nul 2>nul
pushd "%ROOT_DIR%\web"
set "VITE_APP_API_URL=http://127.0.0.1:%SERVER_PORT%"
call %NPM_BIN% run build-prod > "%FRONTEND_BUILD_LOG%" 2>&1
set "FRONTEND_BUILD_EXIT_CODE=%ERRORLEVEL%"
popd
if not "%FRONTEND_BUILD_EXIT_CODE%"=="0" goto prepare_frontend_build_error
if not exist "%FRONT_DIST_INDEX%" goto prepare_frontend_dist_missing
exit /b 0

:write_frontend_runtime_config
if not exist "%FRONT_DIST_DIR%" mkdir "%FRONT_DIST_DIR%" >nul 2>nul
> "%FRONT_RUNTIME_CONFIG%" (
  echo window.__APP_RUNTIME_CONFIG__ = {
  echo   apiBaseUrl: "http://127.0.0.1:%SERVER_PORT%"
  echo };
)
if errorlevel 1 exit /b 1
exit /b 0

:wait_for_port
setlocal
set "WAIT_PORT=%~1"
set "WAIT_SECONDS=%~2"
if "%WAIT_SECONDS%"=="" set "WAIT_SECONDS=15"
for /L %%i in (1,1,%WAIT_SECONDS%) do (
  netstat -ano | findstr /r /c:":%WAIT_PORT% .*LISTENING" >nul 2>nul
  if not errorlevel 1 (
    endlocal & exit /b 0
  )
  timeout /t 1 >nul
)
endlocal & exit /b 1

:kill_port
for /f "tokens=5" %%a in ('netstat -ano ^| findstr /r /c:":%~1 .*LISTENING"') do (
  echo [INFO] Stopping existing PID %%a ^(port %~1^)
  taskkill /PID %%a /F >nul 2>nul
)
exit /b 0

:backend_start_error
echo [ERROR] Backend service failed to start on port %SERVER_PORT%.
echo [ERROR] Please check: %BACKEND_LOG%
if exist "%BACKEND_LOG%" (
  echo.
  echo [ERROR] Last backend log lines:
  powershell -NoProfile -Command "if (Test-Path '%BACKEND_LOG%') { Get-Content -Path '%BACKEND_LOG%' -Tail 40 }"
)
pause
exit /b 1

:frontend_start_error
echo [ERROR] Frontend service failed to start on port %FRONT_PORT%.
echo [ERROR] Please check: %FRONTEND_LOG%
if exist "%FRONTEND_LOG%" (
  echo.
  echo [ERROR] Last frontend log lines:
  powershell -NoProfile -Command "if (Test-Path '%FRONTEND_LOG%') { Get-Content -Path '%FRONTEND_LOG%' -Tail 40 }"
)
pause
exit /b 1

:db_precheck_error
echo [ERROR] Database check failed before backend startup.
echo [ERROR] Please run check-windows.bat or fix DB settings in config-windows.bat.
pause
exit /b 1

:java_error
echo [ERROR] Java was not found in PATH.
echo [ERROR] Please install JDK 17 first.
pause
exit /b 1

:maven_error
echo [ERROR] Maven was not found in PATH.
echo [ERROR] Please install Maven 3.9+ first, or provide target\template.jar.
pause
exit /b 1

:node_error
echo [ERROR] Node.js was not found in PATH.
echo [ERROR] Please install Node.js 18+ first.
pause
exit /b 1

:prepare_frontend_no_npm
echo [ERROR] npm was not found in PATH, and packaged web\\dist is also missing.
echo [ERROR] Please use the latest delivery package, or install npm so the fallback build can run.
pause
exit /b 1

:prepare_frontend_npm_install_error
echo [ERROR] Frontend dependency installation failed.
echo [ERROR] Please check your network, npm registry settings, or clear web\\node_modules and retry.
pause
exit /b 1

:prepare_frontend_build_error
echo [ERROR] Frontend build failed.
echo [ERROR] Please check: %FRONTEND_BUILD_LOG%
if exist "%FRONTEND_BUILD_LOG%" (
  echo.
  echo [ERROR] Last frontend build log lines:
  powershell -NoProfile -Command "if (Test-Path '%FRONTEND_BUILD_LOG%') { Get-Content -Path '%FRONTEND_BUILD_LOG%' -Tail 40 }"
)
pause
exit /b 1

:prepare_frontend_dist_missing
echo [ERROR] Frontend build finished but web\\dist\\index.html was not generated.
pause
exit /b 1

:frontend_runtime_config_error
echo [ERROR] Failed to generate frontend runtime-config.js.
pause
exit /b 1
