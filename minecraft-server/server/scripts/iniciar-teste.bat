@echo off
REM Servidor de teste local do Pangeia — ver ../testar-local.md
REM Coloque este arquivo na mesma pasta do paper.jar.

java -Xms1G -Xmx2G -jar paper.jar nogui

echo.
echo O servidor parou. Feche esta janela ou rode de novo.
pause
