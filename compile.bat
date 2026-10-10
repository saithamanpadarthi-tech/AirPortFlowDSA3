@echo off
echo ============================================================
echo   AirportFlow - Compiling with JDK 21
echo ============================================================

set JDK21="C:\Program Files\Java\jdk-21.0.10\bin\javac.exe"

if exist out rmdir /s /q out
mkdir out

%JDK21% -d out -sourcepath src ^
  src\Main.java ^
  src\dataset\*.java ^
  src\co1\*.java ^
  src\co2\*.java ^
  src\co3\*.java ^
  src\co4\*.java ^
  src\co5\*.java ^
  src\co6\*.java ^
  src\menu\*.java

if %ERRORLEVEL% == 0 (
    echo.
    echo [OK]  Compilation successful! Run 'run.bat' to start.
) else (
    echo.
    echo [ERR] Compilation failed. Check error messages above.
)
pause
