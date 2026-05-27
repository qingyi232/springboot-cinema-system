@echo off
setlocal EnableExtensions

call "%~dp0config-windows.bat"

call :kill_port %SERVER_PORT%
call :kill_port %FRONT_PORT%

echo.
echo [SUCCESS] Stop commands have been sent for ports %SERVER_PORT% and %FRONT_PORT%.
pause
exit /b 0

:kill_port
for /f "tokens=5" %%a in ('netstat -ano ^| findstr /r /c:":%~1 .*LISTENING"') do (
  echo [INFO] Stopping PID %%a ^(port %~1^)
  taskkill /PID %%a /F >nul 2>nul
)
exit /b 0
