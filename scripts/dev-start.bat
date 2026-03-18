@echo off
setlocal
set ROOT=%~dp0..

cd /d "%ROOT%\backend"
start "backend" cmd /k "mvn spring-boot:run"

cd /d "%ROOT%\frontend"
start "frontend" cmd /k "npm run dev:mp-weixin"

echo Backend and frontend started.
endlocal
