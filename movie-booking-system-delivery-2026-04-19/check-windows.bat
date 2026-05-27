@echo off
setlocal EnableExtensions

call "%~dp0config-windows.bat"

where java >nul 2>nul
if errorlevel 1 goto java_error

set "BUILD_EXIT_CODE=0"
if not exist "%ROOT_DIR%\target\template.jar" (
  where %MAVEN_BIN% >nul 2>nul
  if errorlevel 1 goto jar_missing_error
  echo [INFO] Backend jar not found. Building jar package ...
  pushd "%ROOT_DIR%"
  call %MAVEN_BIN% -DskipTests package
  set "BUILD_EXIT_CODE=%ERRORLEVEL%"
  popd
)
if not "%BUILD_EXIT_CODE%"=="0" goto build_error

set "DB_URL=jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
set "DB_USERNAME=%DB_USER%"
set "APP_CHECK_DB=true"

echo [INFO] Running database connectivity check ...
pushd "%ROOT_DIR%"
java -jar target\template.jar --app.check-db=true
set "CHECK_EXIT_CODE=%ERRORLEVEL%"
popd

if not "%CHECK_EXIT_CODE%"=="0" goto check_error

echo.
echo [SUCCESS] Database check passed.
pause
exit /b 0

:java_error
echo [ERROR] Java was not found in PATH.
echo [ERROR] Please install JDK 17 first.
pause
exit /b 1

:jar_missing_error
echo [ERROR] target\template.jar was not found.
echo [ERROR] Maven was also not found in PATH, so the jar cannot be built automatically.
pause
exit /b 1

:build_error
echo [ERROR] Jar build failed.
pause
exit /b 1

:check_error
echo.
echo [ERROR] Database check failed.
pause
exit /b 1
