@echo off
for /f "delims=" %%i in (.env) do set %%i
gradlew.bat bootRun